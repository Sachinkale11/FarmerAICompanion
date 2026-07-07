package com.farmerai.companion.farm.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class LandArea {
    private final BigDecimal value;
    private final String unit;

    public LandArea(BigDecimal value, String unit) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Land area must be greater than zero");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Unit cannot be blank");
        }
        this.value = value;
        this.unit = unit.toUpperCase();
    }

    public BigDecimal getValue() { return value; }
    public String getUnit() { return unit; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LandArea landArea = (LandArea) o;
        return Objects.equals(value, landArea.value) &&
               Objects.equals(unit, landArea.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }
}
