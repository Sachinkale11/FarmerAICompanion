package com.farmerai.companion.farmer.domain.repository;

import com.farmerai.companion.farmer.domain.entity.Farmer;

import java.util.Optional;
import java.util.UUID;

public interface FarmerRepository {
    Farmer save(Farmer farmer);
    Optional<Farmer> findById(UUID id);
    Optional<Farmer> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    boolean existsByPhoneNumber(String phoneNumber);
}
