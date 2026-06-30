package ru.example.addresscomparison.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.example.addresscomparison.client.CoordinatesClient;
import ru.example.addresscomparison.config.ApiProperties;
import ru.example.addresscomparison.dto.CoordinatesDto;
import ru.example.addresscomparison.exception.ApiException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("dadataClient")
@RequiredArgsConstructor
public class DadataCoordinatesClient implements CoordinatesClient {

    private final WebClient webClient;
    private final ApiProperties properties;

    @Override
    public CoordinatesDto getCoordinates(String address) {
        try {
            List<?> response = webClient.post()
                    .uri(properties.getDadata().getUrl())
                    .header("Authorization", "Token " + properties.getDadata().getToken())
                    .header("X-Secret", properties.getDadata().getSecret())
                    .bodyValue(List.of(address))
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            return parseResponse(response);
        } catch (Exception e) {
            log.error("Dadata API call failed for address: {}", address, e);
            throw new ApiException("Dadata API failed", e);
        }
    }

    private CoordinatesDto parseResponse(List<?> response) {
        if (response == null || response.isEmpty()) {
            throw new ApiException("Empty Dadata response");
        }

        Map<?, ?> first = (Map<?, ?>) response.get(0);
        String latStr = first.get("geo_lat") != null ? first.get("geo_lat").toString() : null;
        String lonStr = first.get("geo_lon") != null ? first.get("geo_lon").toString() : null;

        if (latStr == null || lonStr == null) {
            throw new ApiException("Missing coordinates in Dadata response");
        }

        return new CoordinatesDto(
                Double.parseDouble(latStr),
                Double.parseDouble(lonStr)
        );
    }
}