package ru.example.addresscomparison.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.addresscomparison.entity.AddressComparison;

public interface AddressRepository extends JpaRepository<AddressComparison, Long> {
}