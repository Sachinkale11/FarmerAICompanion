package com.farmerai.companion.farm.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FarmResponse(
        UUID id,
        UUID farmerId,
        String name,
        BigDecimal areaValue,
        String areaUnit,
        String soilType,
        String irrigationType,
        BigDecimal gpsLat,
        BigDecimal gpsLng,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
