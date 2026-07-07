package com.farmerai.companion.auth.application.port.input;

import com.farmerai.companion.auth.application.dto.AuthResult;
import com.farmerai.companion.auth.application.dto.LoginCommand;
import com.farmerai.companion.auth.application.dto.RefreshTokenCommand;
import com.farmerai.companion.auth.application.dto.RegisterCommand;

/**
 * Input port defining the authentication use cases.
 *
 * <p>This is the contract that the presentation layer depends on.
 * Implemented by {@code AuthApplicationService}.</p>
 */
public interface AuthUseCase {

    /**
     * Registers a new user and returns access + refresh tokens.
     */
    AuthResult register(RegisterCommand command);

    /**
     * Authenticates a user and returns access + refresh tokens.
     */
    AuthResult login(LoginCommand command);

    /**
     * Refreshes an access token using a valid refresh token.
     * Implements refresh token rotation.
     */
    AuthResult refreshToken(RefreshTokenCommand command);

    /**
     * Revokes a refresh token (logout).
     */
    void logout(String refreshToken);
}
