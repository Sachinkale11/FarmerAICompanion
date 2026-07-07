package com.farmerai.companion.auth.application.service;

import com.farmerai.companion.auth.application.dto.AuthResult;
import com.farmerai.companion.auth.application.dto.LoginCommand;
import com.farmerai.companion.auth.application.dto.RegisterCommand;
import com.farmerai.companion.auth.application.port.input.AuthUseCase;
import com.farmerai.companion.auth.application.port.output.PasswordEncoderPort;
import com.farmerai.companion.auth.application.port.output.RefreshTokenRepositoryPort;
import com.farmerai.companion.auth.application.port.output.RoleRepositoryPort;
import com.farmerai.companion.auth.application.port.output.UserRepositoryPort;
import com.farmerai.companion.auth.domain.entity.RefreshToken;
import com.farmerai.companion.auth.domain.entity.Role;
import com.farmerai.companion.auth.domain.entity.User;
import com.farmerai.companion.auth.domain.exception.InvalidCredentialsException;
import com.farmerai.companion.auth.domain.exception.InvalidTokenException;
import com.farmerai.companion.auth.domain.service.AuthenticationDomainService;
import com.farmerai.companion.auth.application.dto.RefreshTokenCommand;
import com.farmerai.companion.auth.domain.valueobject.Email;
import com.farmerai.companion.auth.domain.valueobject.TokenValue;
import com.farmerai.companion.auth.infrastructure.security.jwt.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service that orchestrates authentication use cases.
 *
 * <p>This service coordinates domain objects and infrastructure ports
 * but contains <strong>no business logic itself</strong>. All business
 * rules are delegated to {@link AuthenticationDomainService}.</p>
 *
 * <p>Implements the {@link AuthUseCase} input port.</p>
 */
@Service
@Transactional
public class AuthApplicationService implements AuthUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthApplicationService.class);
    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;

    private final AuthenticationDomainService domainService;
    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtProvider jwtProvider;

    public AuthApplicationService(AuthenticationDomainService domainService,
                                   UserRepositoryPort userRepositoryPort,
                                   RoleRepositoryPort roleRepositoryPort,
                                   RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                                   PasswordEncoderPort passwordEncoderPort,
                                   JwtProvider jwtProvider) {
        this.domainService = domainService;
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtProvider = jwtProvider;
    }

    // ======================== Register ========================

    @Override
    public AuthResult register(RegisterCommand command) {
        log.info("Processing registration for email: {}", command.email());

        // 1. Validate email not taken
        Email email = new Email(command.email());
        domainService.validateUserNotExists(email, new UserRepositoryPortAdapter(userRepositoryPort));

        // 2. Look up the requested role
        Role role = roleRepositoryPort.findByName(command.roleName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Role '" + command.roleName() + "' not found"
                ));

        // 3. Block self-assignment of ADMIN role
        if ("ADMIN".equalsIgnoreCase(command.roleName())) {
            throw new IllegalArgumentException("ADMIN role cannot be self-assigned during registration");
        }

        // 4. Hash the password
        String encodedPassword = passwordEncoderPort.encode(command.password());


        // 5. Create and persist the user
        User user = domainService.createUser(email, encodedPassword, role);
        user = userRepositoryPort.save(user);

        // 6. Generate tokens
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getEmail().getValue(), user.getRole().getName()
        );
        RefreshToken refreshToken = domainService.createRefreshToken(
                user.getId(), REFRESH_TOKEN_EXPIRY_DAYS,
                command.deviceId(), command.deviceName(), command.platform()
        );
        refreshTokenRepositoryPort.save(refreshToken);

        log.info("User registered successfully: {}", user.getId());

        return AuthResult.of(
                accessToken,
                refreshToken.getToken().getValue(),
                jwtProvider.getAccessTokenExpirationSeconds(),
                user.getId(),
                user.getEmail().getValue(),
                user.getRole().getName(),
                user.getStatus().name()
        );
    }

    // ======================== Login ========================

    @Override
    public AuthResult login(LoginCommand command) {
        log.info("Processing login for email: {}", command.email());

        // 1. Find user by email
        Email email = new Email(command.email());
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // 2. Validate credentials
        domainService.validateCredentials(
                user, command.password(), passwordEncoderPort::matches
        );

        // 3. Validate user is active
        domainService.validateUserActive(user);

        // 4. Generate tokens
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getEmail().getValue(), user.getRole().getName()
        );
        RefreshToken refreshToken = domainService.createRefreshToken(
                user.getId(), REFRESH_TOKEN_EXPIRY_DAYS,
                command.deviceId(), command.deviceName(), command.platform()
        );
        refreshTokenRepositoryPort.save(refreshToken);

        log.info("User logged in successfully: {}", user.getId());

        return AuthResult.of(
                accessToken,
                refreshToken.getToken().getValue(),
                jwtProvider.getAccessTokenExpirationSeconds(),
                user.getId(),
                user.getEmail().getValue(),
                user.getRole().getName(),
                user.getStatus().name()
        );
    }

    // ======================== Refresh Token ========================

    @Override
    public AuthResult refreshToken(RefreshTokenCommand command) {
        log.info("Processing token refresh");

        // 1. Find refresh token in DB
        TokenValue tokenValue = new TokenValue(command.refreshToken());
        RefreshToken existingToken = refreshTokenRepositoryPort.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // 2. Validate — with reuse detection
        try {
            domainService.validateRefreshToken(existingToken);
        } catch (InvalidTokenException e) {
            // Reuse detection: revoked token presented → revoke ALL user tokens
            log.warn("Revoked refresh token reuse detected for user: {}. Revoking all tokens.",
                    existingToken.getUserId());
            refreshTokenRepositoryPort.revokeAllByUserId(existingToken.getUserId());
            throw e;
        }

        // 3. Revoke the old refresh token (rotation)
        existingToken.revoke();
        refreshTokenRepositoryPort.save(existingToken);

        // 4. Load the user
        User user = userRepositoryPort.findById(existingToken.getUserId())
                .orElseThrow(() -> new InvalidTokenException("User associated with token not found"));

        // 5. Generate new token pair
        String newAccessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getEmail().getValue(), user.getRole().getName()
        );
        RefreshToken newRefreshToken = domainService.createRefreshToken(
                user.getId(), REFRESH_TOKEN_EXPIRY_DAYS,
                command.deviceId(), command.deviceName(), command.platform()
        );
        refreshTokenRepositoryPort.save(newRefreshToken);

        log.info("Token refreshed successfully for user: {}", user.getId());

        return AuthResult.of(
                newAccessToken,
                newRefreshToken.getToken().getValue(),
                jwtProvider.getAccessTokenExpirationSeconds(),
                user.getId(),
                user.getEmail().getValue(),
                user.getRole().getName(),
                user.getStatus().name()
        );
    }

    // ======================== Logout ========================

    @Override
    public void logout(String refreshTokenStr) {
        log.info("Processing logout");

        TokenValue tokenValue = new TokenValue(refreshTokenStr);
        refreshTokenRepositoryPort.findByToken(tokenValue)
                .ifPresent(token -> {
                    if (!token.isRevoked()) {
                        token.revoke();
                        refreshTokenRepositoryPort.save(token);
                        log.info("Refresh token revoked for user: {}", token.getUserId());
                    }
                });
        // Always return success (idempotent)
    }

    // ======================== Inner Adapter ========================

    /**
     * Adapts {@link UserRepositoryPort} to the domain's {@link com.farmerai.companion.auth.domain.repository.UserRepository}
     * interface for use in the domain service. This is a lightweight bridge that keeps the
     * domain service independent of application-layer port interfaces.
     */
    private record UserRepositoryPortAdapter(
            UserRepositoryPort port
    ) implements com.farmerai.companion.auth.domain.repository.UserRepository {

        @Override
        public User save(User user) {
            return port.save(user);
        }

        @Override
        public java.util.Optional<User> findByEmail(Email email) {
            return port.findByEmail(email);
        }

        @Override
        public java.util.Optional<User> findById(java.util.UUID id) {
            return port.findById(id);
        }

        @Override
        public boolean existsByEmail(Email email) {
            return port.existsByEmail(email);
        }
    }
}
