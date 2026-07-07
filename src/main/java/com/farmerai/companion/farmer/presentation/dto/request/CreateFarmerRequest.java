package com.farmerai.companion.farmer.presentation.dto.request;

import com.farmerai.companion.farmer.domain.valueobject.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFarmerRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,

        @Size(max = 20)
        String phoneNumber,

        @Size(max = 10)
        String preferredLanguage,

        Address address
) {}
