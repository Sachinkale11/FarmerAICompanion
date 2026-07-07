package com.farmerai.companion.auth.presentation.controller;

import com.farmerai.companion.auth.application.dto.AuthResult;
import com.farmerai.companion.auth.application.dto.LoginCommand;
import com.farmerai.companion.auth.application.dto.RefreshTokenCommand;
import com.farmerai.companion.auth.application.dto.RegisterCommand;
import com.farmerai.companion.auth.application.port.input.AuthUseCase;
import com.farmerai.companion.auth.presentation.dto.request.LoginRequest;
import com.farmerai.companion.auth.presentation.dto.request.LogoutRequest;
import com.farmerai.companion.auth.presentation.dto.request.RefreshTokenRequest;
import com.farmerai.companion.auth.presentation.dto.request.RegisterRequest;
import com.farmerai.companion.auth.presentation.dto.response.AuthResponse;
import com.farmerai.companion.auth.presentation.dto.response.MessageResponse;
import com.farmerai.companion.auth.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints.
 *
 * <p>Handles HTTP concerns: request validation, response mapping, status codes.
 * Delegates all business logic to {@link AuthUseCase}.</p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and token management")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    // ======================== Register ========================

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns access + refresh tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<com.farmerai.companion.shared.response.ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "X-Device-Id", defaultValue = "unknown") String deviceId,
            @RequestHeader(value = "X-Device-Name", defaultValue = "unknown") String deviceName,
            @RequestHeader(value = "X-Platform", defaultValue = "unknown") String platform) {
        log.info("Registration request for email: {}", request.email());

        RegisterCommand command = new RegisterCommand(
                request.email(),
                request.password(),
                request.roleName(),
                deviceId,
                deviceName,
                platform
        );

        AuthResult result = authUseCase.register(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(com.farmerai.companion.shared.response.ApiResponse.success(toAuthResponse(result), "User registered successfully"));
    }

    // ======================== Login ========================

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns access + refresh tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<com.farmerai.companion.shared.response.ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(value = "X-Device-Id", defaultValue = "unknown") String deviceId,
            @RequestHeader(value = "X-Device-Name", defaultValue = "unknown") String deviceName,
            @RequestHeader(value = "X-Platform", defaultValue = "unknown") String platform) {
        log.info("Login request for email: {}", request.email());

        LoginCommand command = new LoginCommand(
                request.email(),
                request.password(),
                deviceId,
                deviceName,
                platform
        );

        AuthResult result = authUseCase.login(command);

        return ResponseEntity.ok(com.farmerai.companion.shared.response.ApiResponse.success(toAuthResponse(result), "Login successful"));
    }

    // ======================== Refresh Token ========================

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Issues a new access token using a valid refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<com.farmerai.companion.shared.response.ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            @RequestHeader(value = "X-Device-Id", defaultValue = "unknown") String deviceId,
            @RequestHeader(value = "X-Device-Name", defaultValue = "unknown") String deviceName,
            @RequestHeader(value = "X-Platform", defaultValue = "unknown") String platform) {
        log.info("Token refresh request");

        RefreshTokenCommand command = new RefreshTokenCommand(
                request.refreshToken(),
                deviceId,
                deviceName,
                platform
        );
        AuthResult result = authUseCase.refreshToken(command);

        return ResponseEntity.ok(com.farmerai.companion.shared.response.ApiResponse.success(toAuthResponse(result), "Token refreshed successfully"));
    }

    // ======================== Logout ========================

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revokes the refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully")
    })
    public ResponseEntity<com.farmerai.companion.shared.response.ApiResponse<MessageResponse>> logout(@Valid @RequestBody LogoutRequest request) {
        log.info("Logout request");

        authUseCase.logout(request.refreshToken());

        return ResponseEntity.ok(com.farmerai.companion.shared.response.ApiResponse.success(new MessageResponse("Logged out successfully"), "Logged out successfully"));
    }

    // ======================== Mapping Helper ========================

    /**
     * Maps the internal AuthResult to the presentation-layer AuthResponse.
     */
    private AuthResponse toAuthResponse(AuthResult result) {
        UserResponse userResponse = null;
        if (result.userId() != null) {
            userResponse = new UserResponse(
                    result.userId(),
                    result.email(),
                    result.role(),
                    result.status()
            );
        }
        return new AuthResponse(
                result.accessToken(),
                result.refreshToken(),
                result.tokenType(),
                result.expiresIn(),
                userResponse
        );
    }
}
