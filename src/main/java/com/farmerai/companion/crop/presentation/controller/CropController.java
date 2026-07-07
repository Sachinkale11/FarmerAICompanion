package com.farmerai.companion.crop.presentation.controller;

import com.farmerai.companion.crop.application.dto.CreateCropCommand;
import com.farmerai.companion.crop.application.dto.CropResponse;
import com.farmerai.companion.crop.application.dto.UpdateCropStatusCommand;
import com.farmerai.companion.crop.application.service.CropApplicationService;
import com.farmerai.companion.crop.domain.valueobject.CropSeason;
import com.farmerai.companion.crop.domain.valueobject.CropStatus;
import com.farmerai.companion.crop.presentation.dto.request.CreateCropRequest;
import com.farmerai.companion.crop.presentation.dto.request.UpdateCropStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CropController {

    private final CropApplicationService cropApplicationService;

    public CropController(CropApplicationService cropApplicationService) {
        this.cropApplicationService = cropApplicationService;
    }

    @PostMapping("/farms/{farmId}/crops")
    public ResponseEntity<CropResponse> planCrop(
            @PathVariable UUID farmId,
            @Valid @RequestBody CreateCropRequest request) {
        
        CropSeason season = CropSeason.valueOf(request.season().toUpperCase());
        
        CreateCropCommand command = new CreateCropCommand(
                request.name(),
                request.variety(),
                season,
                request.sowingDate(),
                request.expectedHarvestDate()
        );
        
        CropResponse response = cropApplicationService.planCrop(farmId, command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/farms/{farmId}/crops")
    public ResponseEntity<List<CropResponse>> getCropsForFarm(
            @PathVariable UUID farmId,
            @RequestParam(required = false) String status) {
        
        List<CropResponse> response = cropApplicationService.getCropsByFarmId(farmId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/crops/{cropId}")
    public ResponseEntity<CropResponse> getCropDetails(@PathVariable UUID cropId) {
        CropResponse response = cropApplicationService.getCropDetails(cropId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/crops/{cropId}/status")
    public ResponseEntity<CropResponse> updateCropStatus(
            @PathVariable UUID cropId,
            @Valid @RequestBody UpdateCropStatusRequest request) {
        
        CropStatus newStatus = CropStatus.valueOf(request.status().toUpperCase());
        UpdateCropStatusCommand command = new UpdateCropStatusCommand(newStatus, request.actionDate());
        
        CropResponse response = cropApplicationService.updateCropStatus(cropId, command);
        return ResponseEntity.ok(response);
    }
}
