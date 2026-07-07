package com.farmerai.companion.farmer.infrastructure.persistence.adapter;

import com.farmerai.companion.farmer.domain.entity.Farmer;
import com.farmerai.companion.farmer.domain.repository.FarmerRepository;
import com.farmerai.companion.farmer.infrastructure.persistence.entity.FarmerJpaEntity;
import com.farmerai.companion.farmer.infrastructure.persistence.repository.FarmerJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class FarmerRepositoryAdapter implements FarmerRepository {

    private final FarmerJpaRepository jpaRepository;

    public FarmerRepositoryAdapter(FarmerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Farmer save(Farmer farmer) {
        FarmerJpaEntity entity = toJpaEntity(farmer);
        FarmerJpaEntity savedEntity = jpaRepository.save(entity);
        return toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Farmer> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public Optional<Farmer> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomainEntity);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return jpaRepository.existsByPhoneNumber(phoneNumber);
    }

    private FarmerJpaEntity toJpaEntity(Farmer domain) {
        if (domain == null) return null;
        FarmerJpaEntity entity = new FarmerJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setPreferredLanguage(domain.getPreferredLanguage());
        entity.setAddress(domain.getAddress());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }

    private Farmer toDomainEntity(FarmerJpaEntity entity) {
        if (entity == null) return null;
        Farmer domain = Farmer.create(
                entity.getUserId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhoneNumber(),
                entity.getPreferredLanguage(),
                entity.getAddress()
        );
        domain.setId(entity.getId());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setVersion(entity.getVersion());
        return domain;
    }
}
