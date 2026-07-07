package com.farmerai.companion.farmer.presentation.controller;

import com.farmerai.companion.farmer.application.dto.CreateFarmerCommand;
import com.farmerai.companion.farmer.application.dto.FarmerResponse;
import com.farmerai.companion.farmer.application.service.FarmerApplicationService;
import com.farmerai.companion.farmer.presentation.dto.request.CreateFarmerRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/farmers")
public class FarmerController {

    private final FarmerApplicationService farmerApplicationService;

    public FarmerController(FarmerApplicationService farmerApplicationService) {
        this.farmerApplicationService = farmerApplicationService;
    }

    @PostMapping
    public ResponseEntity<FarmerResponse> createFarmerProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateFarmerRequest request) {
        
        // Assuming CustomUserDetails provides getId() or username is the UUID
        // Standard Spring Security UserDetails uses username for the principal identifier
        UUID userId = UUID.fromString(userDetails.getUsername());
        
        CreateFarmerCommand command = new CreateFarmerCommand(
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                request.preferredLanguage(),
                request.address()
        );
        
        FarmerResponse response = farmerApplicationService.createFarmer(userId, command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<FarmerResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        FarmerResponse response = farmerApplicationService.getFarmerProfile(userId);
        return ResponseEntity.ok(response);
    }
}
