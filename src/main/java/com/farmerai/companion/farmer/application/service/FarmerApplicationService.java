package com.farmerai.companion.farmer.application.service;

import com.farmerai.companion.farmer.application.dto.CreateFarmerCommand;
import com.farmerai.companion.farmer.application.dto.FarmerResponse;
import com.farmerai.companion.farmer.domain.entity.Farmer;
import com.farmerai.companion.farmer.domain.repository.FarmerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FarmerApplicationService {

    private final FarmerRepository farmerRepository;

    public FarmerApplicationService(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    @Transactional
    public FarmerResponse createFarmer(UUID userId, CreateFarmerCommand command) {
        if (farmerRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Farmer profile already exists for this user");
        }

        if (command.phoneNumber() != null && farmerRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

        Farmer farmer = Farmer.create(
                userId,
                command.firstName(),
                command.lastName(),
                command.phoneNumber(),
                command.preferredLanguage(),
                command.address()
        );

        Farmer savedFarmer = farmerRepository.save(farmer);
        return toResponse(savedFarmer);
    }

    @Transactional(readOnly = true)
    public FarmerResponse getFarmerProfile(UUID userId) {
        Farmer farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer profile not found"));
        return toResponse(farmer);
    }

    private FarmerResponse toResponse(Farmer farmer) {
        return new FarmerResponse(
                farmer.getId(),
                farmer.getUserId(),
                farmer.getFirstName(),
                farmer.getLastName(),
                farmer.getPhoneNumber(),
                farmer.getPreferredLanguage(),
                farmer.getAddress(),
                farmer.getCreatedAt(),
                farmer.getUpdatedAt()
        );
    }
}
