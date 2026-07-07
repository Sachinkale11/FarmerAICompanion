package com.farmerai.companion.crop.application.dto;

import com.farmerai.companion.crop.domain.valueobject.CropSeason;
import com.farmerai.companion.crop.domain.valueobject.CropStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CropResponse(
        UUID id,
        UUID farmId,
        String name,
        String variety,
        CropStatus status,
        CropSeason season,
        LocalDate sowingDate,
        LocalDate expectedHarvestDate,
        LocalDate actualHarvestDate,
        BigDecimal expectedYieldValue,
        String yieldUnit,
        Instant createdAt,
        Instant updatedAt
) {}
