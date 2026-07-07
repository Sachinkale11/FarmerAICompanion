package com.farmerai.companion.crop.infrastructure.persistence.adapter;

import com.farmerai.companion.crop.domain.entity.Crop;
import com.farmerai.companion.crop.domain.repository.CropRepository;
import com.farmerai.companion.crop.domain.valueobject.CropSeason;
import com.farmerai.companion.crop.domain.valueobject.CropStatus;
import com.farmerai.companion.crop.infrastructure.persistence.entity.CropJpaEntity;
import com.farmerai.companion.crop.infrastructure.persistence.repository.CropJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CropRepositoryAdapter implements CropRepository {

    private final CropJpaRepository jpaRepository;

    public CropRepositoryAdapter(CropJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Crop save(Crop crop) {
        CropJpaEntity entity = toJpaEntity(crop);
        CropJpaEntity savedEntity = jpaRepository.save(entity);
        return toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Crop> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public List<Crop> findByFarmId(UUID farmId) {
        return jpaRepository.findByFarmId(farmId).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Crop> findByFarmIdAndStatus(UUID farmId, CropStatus status) {
        return jpaRepository.findByFarmIdAndStatus(farmId, status.name()).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    private CropJpaEntity toJpaEntity(Crop domain) {
        if (domain == null) return null;
        CropJpaEntity entity = new CropJpaEntity();
        entity.setId(domain.getId());
        entity.setFarmId(domain.getFarmId());
        entity.setName(domain.getName());
        entity.setVariety(domain.getVariety());
        entity.setStatus(domain.getStatus().name());
        entity.setSeason(domain.getSeason().name());
        entity.setSowingDate(domain.getSowingDate());
        entity.setExpectedHarvestDate(domain.getExpectedHarvestDate());
        entity.setActualHarvestDate(domain.getActualHarvestDate());
        entity.setExpectedYieldValue(domain.getExpectedYieldValue());
        entity.setYieldUnit(domain.getYieldUnit());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }

    private Crop toDomainEntity(CropJpaEntity entity) {
        if (entity == null) return null;
        CropSeason season = CropSeason.valueOf(entity.getSeason());
        CropStatus status = CropStatus.valueOf(entity.getStatus());
        
        Crop domain = Crop.plan(
                entity.getFarmId(),
                entity.getName(),
                entity.getVariety(),
                season,
                entity.getSowingDate(),
                entity.getExpectedHarvestDate()
        );
        domain.setId(entity.getId());
        domain.setStatus(status);
        domain.setActualHarvestDate(entity.getActualHarvestDate());
        domain.setExpectedYieldValue(entity.getExpectedYieldValue());
        domain.setYieldUnit(entity.getYieldUnit());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setVersion(entity.getVersion());
        return domain;
    }
}
