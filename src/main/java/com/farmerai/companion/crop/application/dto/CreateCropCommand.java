package com.farmerai.companion.crop.application.dto;

import com.farmerai.companion.crop.domain.valueobject.CropSeason;
import java.time.LocalDate;

public record CreateCropCommand(
        String name,
        String variety,
        CropSeason season,
        LocalDate sowingDate,
        LocalDate expectedHarvestDate
) {}
