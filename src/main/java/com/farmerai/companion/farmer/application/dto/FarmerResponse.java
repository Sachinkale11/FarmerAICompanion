package com.farmerai.companion.farmer.application.dto;

import com.farmerai.companion.farmer.domain.valueobject.Address;
import java.time.Instant;
import java.util.UUID;

public record FarmerResponse(
        UUID id,
        UUID userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String preferredLanguage,
        Address address,
        Instant createdAt,
        Instant updatedAt
) {}
