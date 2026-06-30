package ru.example.addresscomparison.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.example.addresscomparison.entity.AddressComparison;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    private AddressRepository repository;

    @Test
    void shouldSaveEntity() {
        AddressComparison entity = AddressComparison.builder()
                .address("test")
                .distance(100.0)
                .createdAt(LocalDateTime.now())
                .build();

        AddressComparison saved = repository.save(entity);
        assertNotNull(saved.getId());
    }
}