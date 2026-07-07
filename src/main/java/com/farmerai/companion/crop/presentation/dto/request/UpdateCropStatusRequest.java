package com.farmerai.companion.crop.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCropStatusRequest(
        @NotBlank(message = "New status is required")
        @Size(max = 50)
        String status,
        
        LocalDate actionDate
) {}
