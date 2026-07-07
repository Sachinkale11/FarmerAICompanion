package com.farmerai.companion.auth.presentation.dto.response;

import java.util.UUID;

/**
 * Response DTO containing basic user information.
 */
public record UserResponse(
        UUID id,
        String email,
        String role,
        String status
) {
}
