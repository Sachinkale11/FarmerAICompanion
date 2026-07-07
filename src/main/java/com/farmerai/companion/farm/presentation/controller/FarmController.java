package com.farmerai.companion.farm.presentation.controller;

import com.farmerai.companion.farm.application.dto.CreateFarmCommand;
import com.farmerai.companion.farm.application.dto.FarmResponse;
import com.farmerai.companion.farm.application.service.FarmApplicationService;
import com.farmerai.companion.farm.presentation.dto.request.CreateFarmRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/farms")
public class FarmController {

    private final FarmApplicationService farmApplicationService;

    public FarmController(FarmApplicationService farmApplicationService) {
        this.farmApplicationService = farmApplicationService;
    }

    @PostMapping
    public ResponseEntity<FarmResponse> registerFarm(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateFarmRequest request) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        
        CreateFarmCommand command = new CreateFarmCommand(
                request.name(),
                request.areaValue(),
                request.areaUnit(),
                request.soilType(),
                request.irrigationType(),
                request.gpsLat(),
                request.gpsLng()
        );
        
        FarmResponse response = farmApplicationService.registerFarm(userId, command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FarmResponse>> getMyFarms(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        List<FarmResponse> response = farmApplicationService.getFarmsForUser(userId);
        return ResponseEntity.ok(response);
    }
}
