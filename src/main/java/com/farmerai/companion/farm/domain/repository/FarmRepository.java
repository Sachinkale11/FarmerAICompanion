package com.farmerai.companion.farm.domain.repository;

import com.farmerai.companion.farm.domain.entity.Farm;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmRepository {
    Farm save(Farm farm);
    Optional<Farm> findById(UUID id);
    List<Farm> findByFarmerId(UUID farmerId);
    boolean existsByFarmerIdAndName(UUID farmerId, String name);
}
