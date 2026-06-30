--liquibase formatted sql

--changeset you:001-create-table
CREATE TABLE address_comparison
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    address    VARCHAR(255) NOT NULL,
    created_at DATETIME,
    dadata_lat DOUBLE,
    dadata_lon DOUBLE,
    yandex_lat DOUBLE,
    yandex_lon DOUBLE,
    distance DOUBLE NOT NULL
);