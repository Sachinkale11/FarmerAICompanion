package com.farmerai.companion.farm.domain.entity;

import com.farmerai.companion.farm.domain.valueobject.GpsCoordinates;
import com.farmerai.companion.farm.domain.valueobject.LandArea;

import java.time.Instant;
import java.util.UUID;

public class Farm {
    private UUID id;
    private UUID farmerId;
    private String name;
    private LandArea area;
    private String soilType;
    private String irrigationType;
    private GpsCoordinates gpsCoordinates;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private Farm(UUID farmerId, String name, LandArea area, String soilType, String irrigationType, GpsCoordinates gpsCoordinates) {
        if (farmerId == null) throw new IllegalArgumentException("Farmer ID cannot be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Farm name cannot be blank");
        if (area == null) throw new IllegalArgumentException("Land area cannot be null");
        if (soilType == null || soilType.isBlank()) throw new IllegalArgumentException("Soil type cannot be blank");

        this.farmerId = farmerId;
        this.name = name;
        this.area = area;
        this.soilType = soilType;
        this.irrigationType = irrigationType;
        this.gpsCoordinates = gpsCoordinates;
        this.isActive = true;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public static Farm register(UUID farmerId, String name, LandArea area, String soilType, String irrigationType, GpsCoordinates gpsCoordinates) {
        return new Farm(farmerId, name, area, soilType, irrigationType, gpsCoordinates);
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = Instant.now();
    }

    // Getters and setters for reconstitution
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFarmerId() { return farmerId; }
    public void setFarmerId(UUID farmerId) { this.farmerId = farmerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LandArea getArea() { return area; }
    public void setArea(LandArea area) { this.area = area; }
    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }
    public String getIrrigationType() { return irrigationType; }
    public void setIrrigationType(String irrigationType) { this.irrigationType = irrigationType; }
    public GpsCoordinates getGpsCoordinates() { return gpsCoordinates; }
    public void setGpsCoordinates(GpsCoordinates gpsCoordinates) { this.gpsCoordinates = gpsCoordinates; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
