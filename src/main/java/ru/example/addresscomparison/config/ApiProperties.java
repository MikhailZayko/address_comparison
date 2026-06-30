package ru.example.addresscomparison.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api")
public class ApiProperties {
    private Yandex yandex;
    private Dadata dadata;

    @Data
    public static class Yandex {
        private String key;
        private String url;
    }

    @Data
    public static class Dadata {
        private String token;
        private String secret;
        private String url;
    }
}