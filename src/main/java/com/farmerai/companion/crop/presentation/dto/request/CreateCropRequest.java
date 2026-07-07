package com.farmerai.companion.crop.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCropRequest(
        @NotBlank(message = "Crop name is required")
        @Size(max = 255)
        String name,

        @Size(max = 255)
        String variety,

        @NotBlank(message = "Season is required")
        @Size(max = 50)
        String season,

        LocalDate sowingDate,
        
        LocalDate expectedHarvestDate
) {}
