package ru.example.addresscomparison.dto;

public record CompareResponse(

        String address,

        CoordinatesDto yandex,

        CoordinatesDto dadata,

        Double distance
) {
}