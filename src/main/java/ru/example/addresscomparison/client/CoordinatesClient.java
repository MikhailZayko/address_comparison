package ru.example.addresscomparison.client;

import ru.example.addresscomparison.dto.CoordinatesDto;

public interface CoordinatesClient {
    CoordinatesDto getCoordinates(String address);
}
