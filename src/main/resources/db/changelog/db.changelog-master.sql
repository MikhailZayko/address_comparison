--liquibase formatted sql

--changeset you:master
--include file:db/changelog/001-create-table.sql
--include file:db/changelog/002-seed-data.sql