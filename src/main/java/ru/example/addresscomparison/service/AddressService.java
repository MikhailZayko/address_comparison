package ru.example.addresscomparison.service;

import ru.example.addresscomparison.dto.AddressRequest;
import ru.example.addresscomparison.dto.CompareResponse;

public interface AddressService {

    CompareResponse compare(AddressRequest request);
}
