package com.farmerai.companion.farm.infrastructure.persistence.adapter;

import com.farmerai.companion.farm.domain.entity.Farm;
import com.farmerai.companion.farm.domain.repository.FarmRepository;
import com.farmerai.companion.farm.domain.valueobject.GpsCoordinates;
import com.farmerai.companion.farm.domain.valueobject.LandArea;
import com.farmerai.companion.farm.infrastructure.persistence.entity.FarmJpaEntity;
import com.farmerai.companion.farm.infrastructure.persistence.repository.FarmJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FarmRepositoryAdapter implements FarmRepository {

    private final FarmJpaRepository jpaRepository;

    public FarmRepositoryAdapter(FarmJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Farm save(Farm farm) {
        FarmJpaEntity entity = toJpaEntity(farm);
        FarmJpaEntity savedEntity = jpaRepository.save(entity);
        return toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Farm> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public List<Farm> findByFarmerId(UUID farmerId) {
        return jpaRepository.findByFarmerId(farmerId).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByFarmerIdAndName(UUID farmerId, String name) {
        return jpaRepository.existsByFarmerIdAndName(farmerId, name);
    }

    private FarmJpaEntity toJpaEntity(Farm domain) {
        if (domain == null) return null;
        FarmJpaEntity entity = new FarmJpaEntity();
        entity.setId(domain.getId());
        entity.setFarmerId(domain.getFarmerId());
        entity.setName(domain.getName());
        if (domain.getArea() != null) {
            entity.setAreaValue(domain.getArea().getValue());
            entity.setAreaUnit(domain.getArea().getUnit());
        }
        entity.setSoilType(domain.getSoilType());
        entity.setIrrigationType(domain.getIrrigationType());
        if (domain.getGpsCoordinates() != null) {
            entity.setGpsLat(domain.getGpsCoordinates().getLatitude());
            entity.setGpsLng(domain.getGpsCoordinates().getLongitude());
        }
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }

    private Farm toDomainEntity(FarmJpaEntity entity) {
        if (entity == null) return null;
        LandArea area = new LandArea(entity.getAreaValue(), entity.getAreaUnit());
        GpsCoordinates gps = null;
        if (entity.getGpsLat() != null && entity.getGpsLng() != null) {
            gps = new GpsCoordinates(entity.getGpsLat(), entity.getGpsLng());
        }
        
        Farm domain = Farm.register(
                entity.getFarmerId(),
                entity.getName(),
                area,
                entity.getSoilType(),
                entity.getIrrigationType(),
                gps
        );
        domain.setId(entity.getId());
        domain.setActive(entity.isActive());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setVersion(entity.getVersion());
        return domain;
    }
}
