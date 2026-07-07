package com.farmerai.companion.farm.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateFarmRequest(
        @NotBlank(message = "Farm name is required")
        @Size(max = 255)
        String name,

        @NotNull(message = "Area value is required")
        @Positive(message = "Area value must be positive")
        BigDecimal areaValue,

        @NotBlank(message = "Area unit is required")
        @Size(max = 50)
        String areaUnit,

        @NotBlank(message = "Soil type is required")
        @Size(max = 50)
        String soilType,

        @Size(max = 100)
        String irrigationType,

        BigDecimal gpsLat,
        
        BigDecimal gpsLng
) {}
