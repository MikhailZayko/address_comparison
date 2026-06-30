package ru.example.addresscomparison.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.example.addresscomparison.dto.CoordinatesDto;
import ru.example.addresscomparison.exception.ApiException;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorServiceImplTest {

    private final DistanceCalculatorServiceImpl calculator = new DistanceCalculatorServiceImpl();

    @Test
    void shouldCalculateDistanceCorrectly() {
        CoordinatesDto moscow = new CoordinatesDto(55.7558, 37.6173);
        CoordinatesDto spb = new CoordinatesDto(59.9343, 30.3351);

        double result = calculator.calculateMetersBetween(moscow, spb);
        assertTrue(result > 630_000 && result < 640_000,
                "Expected ~634 km, got: " + result / 1000 + " km");
    }

    @ParameterizedTest
    @CsvSource({
            "0.0, 0.0, 0.0, 0.0, 0.0",
            "0.0, 0.0, 1.0, 0.0, 111195.0",
            "0.0, 0.0, 0.0, 1.0, 111195.0"
    })
    void shouldCalculateDistanceForKnownPoints(double lat1, double lon1,
                                               double lat2, double lon2,
                                               double expected) {
        CoordinatesDto c1 = new CoordinatesDto(lat1, lon1);
        CoordinatesDto c2 = new CoordinatesDto(lat2, lon2);

        double result = calculator.calculateMetersBetween(c1, c2);
        assertEquals(expected, result, 1.0,
                "Expected " + expected + ", got: " + result);
    }

    @Test
    void shouldThrowExceptionWhenCoordinatesAreNull() {
        assertThrows(ApiException.class, () ->
                calculator.calculateMetersBetween(null, new CoordinatesDto(0.0, 0.0))
        );
        assertThrows(ApiException.class, () ->
                calculator.calculateMetersBetween(new CoordinatesDto(0.0, 0.0), null)
        );
    }

    @Test
    void shouldThrowExceptionWhenLatitudeOutOfRange() {
        assertThrows(ApiException.class, () ->
                calculator.calculateMetersBetween(
                        new CoordinatesDto(100.0, 0.0),
                        new CoordinatesDto(0.0, 0.0)
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenLongitudeOutOfRange() {
        assertThrows(ApiException.class, () ->
                calculator.calculateMetersBetween(
                        new CoordinatesDto(0.0, 200.0),
                        new CoordinatesDto(0.0, 0.0)
                )
        );
    }
}