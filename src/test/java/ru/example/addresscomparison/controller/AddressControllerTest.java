package ru.example.addresscomparison.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.addresscomparison.dto.*;
import ru.example.addresscomparison.service.AddressService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AddressService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldReturnResponse() throws Exception {

        when(service.compare(new AddressRequest("test")))
                .thenReturn(new CompareResponse(
                        "test",
                        new CoordinatesDto(1.0, 1.0),
                        new CoordinatesDto(2.0, 2.0),
                        100.0
                ));

        mockMvc.perform(post("/api/addresses/compare")
                        .contentType("application/json")
                        .content("{\"address\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("test"));
    }
}