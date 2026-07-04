package ru.example.addresscomparison.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.example.addresscomparison.client.CoordinatesClient;
import ru.example.addresscomparison.config.ApiProperties;
import ru.example.addresscomparison.dto.CoordinatesDto;
import ru.example.addresscomparison.exception.ApiException;

@Slf4j
@Component("yandexClient")
@RequiredArgsConstructor
public class YandexCoordinatesClient implements CoordinatesClient {

    private final WebClient webClient;
    private final ApiProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CoordinatesDto getCoordinates(String address) {
        String url = UriComponentsBuilder.fromHttpUrl(properties.getYandex().getUrl())
                .queryParam("apikey", properties.getYandex().getKey())
                .queryParam("geocode", address)
                .queryParam("format", "json")
                .build()
                .toUriString();

        log.debug("Yandex request URL: {}", url);

        String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseResponse(response);
    }

    private CoordinatesDto parseResponse(String json) {
        JsonNode root;
        try {
            root = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String pos = root.path("response")
                .path("GeoObjectCollection")
                .path("featureMember")
                .path(0)
                .path("GeoObject")
                .path("Point")
                .path("pos")
                .asText();

        if (pos.isEmpty()) {
            throw new ApiException("No coordinates found in Yandex response");
        }

        String[] parts = pos.split(" ");

        if (parts.length != 2) {
            throw new ApiException("Invalid response from Yandex Client");
        }
        double lon = Double.parseDouble(parts[0]);
        double lat = Double.parseDouble(parts[1]);

        return new CoordinatesDto(lat, lon);

    }
}