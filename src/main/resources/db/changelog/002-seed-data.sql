--liquibase formatted sql

--changeset you:002-seed-data
INSERT INTO address_comparison (address,
                                created_at,
                                dadata_lat,
                                dadata_lon,
                                yandex_lat,
                                yandex_lon,
                                distance)
VALUES ('Москва, Красная площадь',
        NOW(),
        55.7539,
        37.6208,
        55.7539,
        37.6208,
        0.0);