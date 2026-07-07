package com.farmerai.companion.crop.domain.repository;

import com.farmerai.companion.crop.domain.entity.Crop;
import com.farmerai.companion.crop.domain.valueobject.CropStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CropRepository {
    Crop save(Crop crop);
    Optional<Crop> findById(UUID id);
    List<Crop> findByFarmId(UUID farmId);
    List<Crop> findByFarmIdAndStatus(UUID farmId, CropStatus status);
}
