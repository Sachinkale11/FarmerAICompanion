package com.farmerai.companion.crop.domain.valueobject;

public enum CropStatus {
    PLANNED,
    SOWN,
    VEGETATIVE,
    FLOWERING,
    HARVESTED,
    FAILED;

    public boolean canTransitionTo(CropStatus newStatus) {
        if (this == newStatus) return true;
        if (newStatus == FAILED) return true;
        
        return switch (this) {
            case PLANNED -> newStatus == SOWN;
            case SOWN -> newStatus == VEGETATIVE;
            case VEGETATIVE -> newStatus == FLOWERING;
            case FLOWERING -> newStatus == HARVESTED;
            case HARVESTED, FAILED -> false;
        };
    }
}
