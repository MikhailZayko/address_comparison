package ru.example.addresscomparison.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.example.addresscomparison.client.CoordinatesClient;
import ru.example.addresscomparison.dto.AddressRequest;
import ru.example.addresscomparison.dto.CompareResponse;
import ru.example.addresscomparison.dto.CoordinatesDto;
import ru.example.addresscomparison.entity.AddressComparison;
import ru.example.addresscomparison.exception.ApiException;
import ru.example.addresscomparison.repository.AddressRepository;
import ru.example.addresscomparison.service.AddressService;
import ru.example.addresscomparison.service.DistanceCalculatorService;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    private final CoordinatesClient yandexClient;
    private final CoordinatesClient dadataClient;
    private final DistanceCalculatorService distanceCalculator;
    private final AddressRepository repository;

    private static final int MAX_RETRIES = 2;
    private static final long TIMEOUT_SECONDS = 10;

    public AddressServiceImpl(
            @Qualifier("yandexClient") CoordinatesClient yandexClient,
            @Qualifier("dadataClient") CoordinatesClient dadataClient,
            DistanceCalculatorService distanceCalculator,
            AddressRepository repository) {
        this.yandexClient = yandexClient;
        this.dadataClient = dadataClient;
        this.distanceCalculator = distanceCalculator;
        this.repository = repository;
    }

    @Override
    public CompareResponse compare(AddressRequest request) {
        String address = request.address();
        log.info("Starting address comparison: {}", address);

        try {
            CompletableFuture<CoordinatesDto> yandexFuture = CompletableFuture.supplyAsync(() ->
                    callWithRetry(() -> yandexClient.getCoordinates(address), "Yandex", address)
            );

            CompletableFuture<CoordinatesDto> dadataFuture = CompletableFuture.supplyAsync(() ->
                    callWithRetry(() -> dadataClient.getCoordinates(address), "Dadata", address)
            );

            CompletableFuture.allOf(yandexFuture, dadataFuture).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            CoordinatesDto yandexCoords = yandexFuture.get();
            CoordinatesDto dadataCoords = dadataFuture.get();

            log.info("Yandex coords: lat={}, lon={}", yandexCoords.latitude(), yandexCoords.longitude());
            log.info("Dadata coords: lat={}, lon={}", dadataCoords.latitude(), dadataCoords.longitude());

            double distance = distanceCalculator.calculateMetersBetween(yandexCoords, dadataCoords);
            log.info("Calculated distance: {} meters", distance);

            AddressComparison entity = buildEntity(address, yandexCoords, dadataCoords, distance);
            repository.save(entity);
            log.info("Saved comparison result to database");

            return new CompareResponse(address, yandexCoords, dadataCoords, distance);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error during address comparison: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new ApiException("Failed to compare address: " + e.getMessage(), e);
        }
    }

    private CoordinatesDto callWithRetry(Supplier<CoordinatesDto> supplier,
                                         String clientName, String address) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                log.debug("{} attempt {}/{} for address: {}", clientName, attempt, MAX_RETRIES, address);
                return supplier.get();
            } catch (ApiException e) {
                lastException = e;
                log.warn("{} attempt {} failed: {}", clientName, attempt, e.getMessage());
            }
        }

        log.error("{} failed after {} attempts", clientName, MAX_RETRIES);
        throw new ApiException(clientName + " API failed after " + MAX_RETRIES + " attempts", lastException);
    }

    private AddressComparison buildEntity(String address, CoordinatesDto yandex, CoordinatesDto dadata, double distance) {
        return AddressComparison.builder()
                .address(address)
                .yandexLat(yandex.latitude())
                .yandexLon(yandex.longitude())
                .dadataLat(dadata.latitude())
                .dadataLon(dadata.longitude())
                .distance(distance)
                .createdAt(LocalDateTime.now())
                .build();
    }
}