package com.farmerai.companion.farm.application.service;

import com.farmerai.companion.farm.application.dto.CreateFarmCommand;
import com.farmerai.companion.farm.application.dto.FarmResponse;
import com.farmerai.companion.farm.domain.entity.Farm;
import com.farmerai.companion.farm.domain.repository.FarmRepository;
import com.farmerai.companion.farm.domain.valueobject.GpsCoordinates;
import com.farmerai.companion.farm.domain.valueobject.LandArea;
import com.farmerai.companion.farmer.domain.repository.FarmerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FarmApplicationService {

    private final FarmRepository farmRepository;
    private final FarmerRepository farmerRepository; // Inter-domain dependency at app level

    public FarmApplicationService(FarmRepository farmRepository, FarmerRepository farmerRepository) {
        this.farmRepository = farmRepository;
        this.farmerRepository = farmerRepository;
    }

    @Transactional
    public FarmResponse registerFarm(UUID userId, CreateFarmCommand command) {
        // Find the farmer associated with this user
        var farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Farmer profile not found for this user"));

        UUID farmerId = farmer.getId();

        if (farmRepository.existsByFarmerIdAndName(farmerId, command.name())) {
            throw new IllegalArgumentException("A farm with this name already exists for this farmer");
        }

        LandArea area = new LandArea(command.areaValue(), command.areaUnit());

        GpsCoordinates gps = null;
        if (command.gpsLat() != null && command.gpsLng() != null) {
            gps = new GpsCoordinates(command.gpsLat(), command.gpsLng());
        }

        Farm farm = Farm.register(
                farmerId,
                command.name(),
                area,
                command.soilType(),
                command.irrigationType(),
                gps);

        Farm savedFarm = farmRepository.save(farm);
        return toResponse(savedFarm);
    }

    @Transactional(readOnly = true)
    public List<FarmResponse> getFarmsForUser(UUID userId) {
        var farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Farmer profile not found"));

        return farmRepository.findByFarmerId(farmer.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private FarmResponse toResponse(Farm farm) {
        BigDecimal lat = farm.getGpsCoordinates() != null ? farm.getGpsCoordinates().getLatitude() : null;
        BigDecimal lng = farm.getGpsCoordinates() != null ? farm.getGpsCoordinates().getLongitude() : null;

        return new FarmResponse(
                farm.getId(),
                farm.getFarmerId(),
                farm.getName(),
                farm.getArea().getValue(),
                farm.getArea().getUnit(),
                farm.getSoilType(),
                farm.getIrrigationType(),
                lat,
                lng,
                farm.isActive(),
                farm.getCreatedAt(),
                farm.getUpdatedAt());
    }
}
