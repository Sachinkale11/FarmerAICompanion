package com.farmerai.companion.farm.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class GpsCoordinates {
    private final BigDecimal latitude;
    private final BigDecimal longitude;

    public GpsCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude != null && (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude != null && (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GpsCoordinates that = (GpsCoordinates) o;
        return Objects.equals(latitude, that.latitude) &&
               Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
