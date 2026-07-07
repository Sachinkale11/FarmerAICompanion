package com.farmerai.companion.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for user logout.
 */
public record LogoutRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
