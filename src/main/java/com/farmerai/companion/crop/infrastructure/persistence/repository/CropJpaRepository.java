package com.farmerai.companion.crop.infrastructure.persistence.repository;

import com.farmerai.companion.crop.infrastructure.persistence.entity.CropJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CropJpaRepository extends JpaRepository<CropJpaEntity, UUID> {
    List<CropJpaEntity> findByFarmId(UUID farmId);
    List<CropJpaEntity> findByFarmIdAndStatus(UUID farmId, String status);
}
