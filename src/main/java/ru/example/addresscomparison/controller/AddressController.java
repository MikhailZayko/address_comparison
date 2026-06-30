package ru.example.addresscomparison.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.addresscomparison.dto.AddressRequest;
import ru.example.addresscomparison.dto.CompareResponse;
import ru.example.addresscomparison.service.AddressService;

@Slf4j
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/compare")
    public CompareResponse compare(@Valid @RequestBody AddressRequest request) {
        log.info("Received comparison request for address: {}", request.address());
        CompareResponse response = addressService.compare(request);
        log.info("Comparison completed successfully for address: {}", request.address());
        return response;
    }
}
