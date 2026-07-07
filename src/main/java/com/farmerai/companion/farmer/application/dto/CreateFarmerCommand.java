package com.farmerai.companion.farmer.application.dto;

import com.farmerai.companion.farmer.domain.valueobject.Address;

public record CreateFarmerCommand(
        String firstName,
        String lastName,
        String phoneNumber,
        String preferredLanguage,
        Address address
) {}
