package ru.example.addresscomparison.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.example.addresscomparison.client.CoordinatesClient;
import ru.example.addresscomparison.dto.AddressRequest;
import ru.example.addresscomparison.dto.CompareResponse;
import ru.example.addresscomparison.dto.CoordinatesDto;
import ru.example.addresscomparison.exception.ApiException;
import ru.example.addresscomparison.repository.AddressRepository;
import ru.example.addresscomparison.service.DistanceCalculatorService;

import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock(name = "yandexClient")
    private CoordinatesClient yandexClient;

    @Mock(name = "dadataClient")
    private CoordinatesClient dadataClient;

    @Mock
    private DistanceCalculatorService distanceCalculator;

    @Mock
    private AddressRepository repository;

    private AddressServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AddressServiceImpl(yandexClient, dadataClient, distanceCalculator, repository);
    }

    @Test
    void shouldCompareAddressSuccessfully() {
        String address = "Москва, Красная площадь";
        CoordinatesDto yandexCoords = new CoordinatesDto(55.7539, 37.6208);
        CoordinatesDto dadataCoords = new CoordinatesDto(55.7540, 37.6209);
        double expectedDistance = 15.5;
        when(yandexClient.getCoordinates(address)).thenReturn(yandexCoords);
        when(dadataClient.getCoordinates(address)).thenReturn(dadataCoords);
        when(distanceCalculator.calculateMetersBetween(yandexCoords, dadataCoords)).thenReturn(expectedDistance);
        AddressRequest request = new AddressRequest(address);
        CompareResponse response = service.compare(request);
        assertNotNull(response);
        assertEquals(address, response.address());
        assertEquals(yandexCoords, response.yandex());
        assertEquals(dadataCoords, response.dadata());
        assertEquals(expectedDistance, response.distance());
        verify(yandexClient, times(1)).getCoordinates(address);
        verify(dadataClient, times(1)).getCoordinates(address);
        verify(distanceCalculator, times(1)).calculateMetersBetween(yandexCoords, dadataCoords);
        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldRetryOnFailureAndSucceed() {
        String address = "Test address";
        CoordinatesDto yandexCoords = new CoordinatesDto(55.0, 37.0);
        CoordinatesDto dadataCoords = new CoordinatesDto(56.0, 38.0);
        when(yandexClient.getCoordinates(address))
                .thenThrow(new RuntimeException("Temporary error"))
                .thenReturn(yandexCoords);
        when(dadataClient.getCoordinates(address)).thenReturn(dadataCoords);
        when(distanceCalculator.calculateMetersBetween(yandexCoords, dadataCoords)).thenReturn(100.0);
        AddressRequest request = new AddressRequest(address);
        CompareResponse response = service.compare(request);
        assertNotNull(response);
        assertEquals(yandexCoords, response.yandex());
        verify(yandexClient, times(2)).getCoordinates(address);
        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAllRetriesFail() {
        String address = "Fail address";
        when(yandexClient.getCoordinates(address))
                .thenThrow(new RuntimeException("Permanent error"));
        when(dadataClient.getCoordinates(address))
                .thenReturn(new CoordinatesDto(55.0, 37.0));
        AddressRequest request = new AddressRequest(address);
        CompletionException completionException = assertThrows(CompletionException.class,
                () -> service.compare(request));
        assertTrue(completionException.getCause() instanceof ApiException);
        ApiException apiException = (ApiException) completionException.getCause();
        assertTrue(apiException.getMessage().contains("Yandex API failed after 2 attempts"));

        verify(yandexClient, times(2)).getCoordinates(address);
        verify(dadataClient, times(1)).getCoordinates(address);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldNotRetryOn404Error() {
        String address = "Not found address";

        when(yandexClient.getCoordinates(address))
                .thenThrow(new ApiException("404 Not found"));

        when(dadataClient.getCoordinates(address))
                .thenReturn(new CoordinatesDto(55.0, 37.0));

        AddressRequest request = new AddressRequest(address);

        CompletionException completionException = assertThrows(CompletionException.class,
                () -> service.compare(request));
        assertTrue(completionException.getCause() instanceof ApiException);
        ApiException apiException = (ApiException) completionException.getCause();
        assertTrue(apiException.getMessage().contains("404"));

        verify(yandexClient, times(1)).getCoordinates(address);
        verify(dadataClient, times(1)).getCoordinates(address);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldHandleTimeoutAndInterrupt() {
        String address = "Timeout address";
        when(yandexClient.getCoordinates(address)).thenAnswer(invocation -> {
            Thread.sleep(15000);
            return new CoordinatesDto(55.0, 37.0);
        });
        when(dadataClient.getCoordinates(address)).thenReturn(new CoordinatesDto(56.0, 38.0));
        when(distanceCalculator.calculateMetersBetween(any(), any())).thenReturn(0.0);
        AddressRequest request = new AddressRequest(address);
        assertDoesNotThrow(() -> service.compare(request));
        verify(yandexClient, times(1)).getCoordinates(address);
        verify(dadataClient, times(1)).getCoordinates(address);
        verify(repository, times(1)).save(any());
    }
}