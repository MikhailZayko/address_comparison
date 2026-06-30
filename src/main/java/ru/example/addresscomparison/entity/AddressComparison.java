package ru.example.addresscomparison.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "address_comparison")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    @Column(name = "yandex_lat")
    private Double yandexLat;

    @Column(name = "yandex_lon")
    private Double yandexLon;

    @Column(name = "dadata_lat")
    private Double dadataLat;

    @Column(name = "dadata_lon")
    private Double dadataLon;

    @Column(nullable = false)
    private Double distance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
