package com.farmerai.companion.crop.domain.entity;

import com.farmerai.companion.crop.domain.valueobject.CropSeason;
import com.farmerai.companion.crop.domain.valueobject.CropStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Crop {
    private UUID id;
    private UUID farmId;
    private String name;
    private String variety;
    private CropStatus status;
    private CropSeason season;
    private LocalDate sowingDate;
    private LocalDate expectedHarvestDate;
    private LocalDate actualHarvestDate;
    private BigDecimal expectedYieldValue;
    private String yieldUnit;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private Crop(UUID farmId, String name, String variety, CropSeason season, LocalDate sowingDate, LocalDate expectedHarvestDate) {
        if (farmId == null) throw new IllegalArgumentException("Farm ID cannot be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Crop name cannot be blank");
        if (season == null) throw new IllegalArgumentException("Season cannot be null");
        
        if (sowingDate != null && expectedHarvestDate != null && expectedHarvestDate.isBefore(sowingDate)) {
            throw new IllegalArgumentException("Expected harvest date must be after sowing date");
        }
        
        this.farmId = farmId;
        this.name = name;
        this.variety = variety;
        this.status = CropStatus.PLANNED;
        this.season = season;
        this.sowingDate = sowingDate;
        this.expectedHarvestDate = expectedHarvestDate;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public static Crop plan(UUID farmId, String name, String variety, CropSeason season, LocalDate sowingDate, LocalDate expectedHarvestDate) {
        return new Crop(farmId, name, variety, season, sowingDate, expectedHarvestDate);
    }

    public void transitionStatus(CropStatus newStatus, LocalDate actionDate) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Cannot transition crop status from " + this.status + " to " + newStatus);
        }
        
        this.status = newStatus;
        if (newStatus == CropStatus.HARVESTED) {
            this.actualHarvestDate = actionDate != null ? actionDate : LocalDate.now();
        }
        this.updatedAt = Instant.now();
    }
    
    public void setExpectedYield(BigDecimal expectedYieldValue, String yieldUnit) {
        if (expectedYieldValue != null && expectedYieldValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Expected yield cannot be negative");
        }
        this.expectedYieldValue = expectedYieldValue;
        this.yieldUnit = yieldUnit;
        this.updatedAt = Instant.now();
    }

    // Getters and setters for reconstitution
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFarmId() { return farmId; }
    public void setFarmId(UUID farmId) { this.farmId = farmId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public CropStatus getStatus() { return status; }
    public void setStatus(CropStatus status) { this.status = status; }
    public CropSeason getSeason() { return season; }
    public void setSeason(CropSeason season) { this.season = season; }
    public LocalDate getSowingDate() { return sowingDate; }
    public void setSowingDate(LocalDate sowingDate) { this.sowingDate = sowingDate; }
    public LocalDate getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }
    public LocalDate getActualHarvestDate() { return actualHarvestDate; }
    public void setActualHarvestDate(LocalDate actualHarvestDate) { this.actualHarvestDate = actualHarvestDate; }
    public BigDecimal getExpectedYieldValue() { return expectedYieldValue; }
    public void setExpectedYieldValue(BigDecimal expectedYieldValue) { this.expectedYieldValue = expectedYieldValue; }
    public String getYieldUnit() { return yieldUnit; }
    public void setYieldUnit(String yieldUnit) { this.yieldUnit = yieldUnit; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
