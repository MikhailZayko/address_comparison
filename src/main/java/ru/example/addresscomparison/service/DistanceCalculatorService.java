package ru.example.addresscomparison.service;

import ru.example.addresscomparison.dto.CoordinatesDto;

public interface DistanceCalculatorService {
    double calculateMetersBetween(CoordinatesDto c1, CoordinatesDto c2);
}