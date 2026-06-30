package ru.example.addresscomparison;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.example.addresscomparison.config.ApiProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApiProperties.class)
public class AddressComparisonApplication {

    public static void main(String[] args) {
        SpringApplication.run(AddressComparisonApplication.class, args);
    }
}
