package com.farmerai.companion.crop.application.dto;

import com.farmerai.companion.crop.domain.valueobject.CropStatus;
import java.time.LocalDate;

public record UpdateCropStatusCommand(
        CropStatus newStatus,
        LocalDate actionDate
) {}
