package com.farmerai.companion.farm.application.dto;

import java.math.BigDecimal;

public record CreateFarmCommand(
        String name,
        BigDecimal areaValue,
        String areaUnit,
        String soilType,
        String irrigationType,
        BigDecimal gpsLat,
        BigDecimal gpsLng
) {}
