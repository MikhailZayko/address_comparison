package ru.example.addresscomparison.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(

        @NotBlank(message = "Address must not be empty")
        String address
) {
}
