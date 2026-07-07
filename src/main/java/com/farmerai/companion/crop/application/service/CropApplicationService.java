package com.farmerai.companion.crop.application.service;

import com.farmerai.companion.crop.application.dto.CreateCropCommand;
import com.farmerai.companion.crop.application.dto.CropResponse;
import com.farmerai.companion.crop.application.dto.UpdateCropStatusCommand;
import com.farmerai.companion.crop.domain.entity.Crop;
import com.farmerai.companion.crop.domain.repository.CropRepository;
import com.farmerai.companion.crop.domain.valueobject.CropStatus;
import com.farmerai.companion.farm.domain.repository.FarmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CropApplicationService {

    private final CropRepository cropRepository;
    private final FarmRepository farmRepository;

    public CropApplicationService(CropRepository cropRepository, FarmRepository farmRepository) {
        this.cropRepository = cropRepository;
        this.farmRepository = farmRepository;
    }

    @Transactional
    public CropResponse planCrop(UUID farmId, CreateCropCommand command) {
        if (farmRepository.findById(farmId).isEmpty()) {
            throw new IllegalArgumentException("Farm does not exist");
        }

        Crop crop = Crop.plan(
                farmId,
                command.name(),
                command.variety(),
                command.season(),
                command.sowingDate(),
                command.expectedHarvestDate()
        );

        Crop savedCrop = cropRepository.save(crop);
        return toResponse(savedCrop);
    }

    @Transactional
    public CropResponse updateCropStatus(UUID cropId, UpdateCropStatusCommand command) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new IllegalArgumentException("Crop not found"));

        crop.transitionStatus(command.newStatus(), command.actionDate());
        
        Crop savedCrop = cropRepository.save(crop);
        return toResponse(savedCrop);
    }

    @Transactional(readOnly = true)
    public List<CropResponse> getCropsByFarmId(UUID farmId, String status) {
        if (farmRepository.findById(farmId).isEmpty()) {
            throw new IllegalArgumentException("Farm does not exist");
        }
        
        List<Crop> crops;
        if (status != null && !status.isBlank()) {
            CropStatus cropStatus = CropStatus.valueOf(status.toUpperCase());
            crops = cropRepository.findByFarmIdAndStatus(farmId, cropStatus);
        } else {
            crops = cropRepository.findByFarmId(farmId);
        }
        
        return crops.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CropResponse getCropDetails(UUID cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new IllegalArgumentException("Crop not found"));
        return toResponse(crop);
    }

    private CropResponse toResponse(Crop crop) {
        return new CropResponse(
                crop.getId(),
                crop.getFarmId(),
                crop.getName(),
                crop.getVariety(),
                crop.getStatus(),
                crop.getSeason(),
                crop.getSowingDate(),
                crop.getExpectedHarvestDate(),
                crop.getActualHarvestDate(),
                crop.getExpectedYieldValue(),
                crop.getYieldUnit(),
                crop.getCreatedAt(),
                crop.getUpdatedAt()
        );
    }
}
