package ru.example.addresscomparison.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.example.addresscomparison.dto.CoordinatesDto;
import ru.example.addresscomparison.exception.ApiException;
import ru.example.addresscomparison.service.DistanceCalculatorService;

@Slf4j
@Service
public class DistanceCalculatorServiceImpl implements DistanceCalculatorService {

    private static final double EARTH_RADIUS_METERS = 6371000;

    @Override
    public double calculateMetersBetween(CoordinatesDto c1, CoordinatesDto c2) {
        validateCoordinates(c1);
        validateCoordinates(c2);

        double lat1 = Math.toRadians(c1.latitude());
        double lon1 = Math.toRadians(c1.longitude());
        double lat2 = Math.toRadians(c2.latitude());
        double lon2 = Math.toRadians(c2.longitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS_METERS * c;
    }

    private void validateCoordinates(CoordinatesDto coords) {
        if (coords == null) {
            throw new ApiException("Coordinates cannot be null");
        }
        if (coords.latitude() == null || coords.longitude() == null) {
            throw new ApiException("Latitude and longitude must not be null");
        }

        double lat = coords.latitude();
        double lon = coords.longitude();

        if (lat < -90 || lat > 90) {
            throw new ApiException("Latitude must be between -90 and 90, got: " + lat);
        }
        if (lon < -180 || lon > 180) {
            throw new ApiException("Longitude must be between -180 and 180, got: " + lon);
        }
    }
}
