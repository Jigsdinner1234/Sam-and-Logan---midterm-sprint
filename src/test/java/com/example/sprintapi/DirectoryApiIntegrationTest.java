package com.example.sprintapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DirectoryApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsRelationsPaginatesSortsAndSupportsCrud() throws Exception {
        String city = mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Denver","state":"Colorado","population":715000}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();
        long cityId = objectMapper.readTree(city).get("id").asLong();

        String secondCity = mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Seattle","state":"Washington","population":755000}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long secondCityId = objectMapper.readTree(secondCity).get("id").asLong();

        String airport = mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Denver International","code":"DEN","cityId":%d,"aircraftIds":[]}
                                """.formatted(cityId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cityId").value(cityId))
                .andReturn().getResponse().getContentAsString();
        long airportId = objectMapper.readTree(airport).get("id").asLong();

        String passenger = mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Alex","lastName":"Morgan","phoneNumber":"555-0101","cityId":%d,"aircraftIds":[]}
                                """.formatted(cityId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cityId").value(cityId))
                .andReturn().getResponse().getContentAsString();
        long passengerId = objectMapper.readTree(passenger).get("id").asLong();

        String aircraft = mockMvc.perform(post("/api/aircraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"737","airlineName":"Example Air","numberOfPassengers":180,"passengerIds":[%d],"airportIds":[%d]}
                                """.formatted(passengerId, airportId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.airportIds[0]").value(airportId))
                .andExpect(jsonPath("$.passengerIds[0]").value(passengerId))
                .andReturn().getResponse().getContentAsString();
        long aircraftId = objectMapper.readTree(aircraft).get("id").asLong();

        mockMvc.perform(get("/api/cities")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "population,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Seattle"))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(put("/api/cities/{id}", cityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Denver Metro","state":"Colorado","population":720000}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Denver Metro"))
                .andExpect(jsonPath("$.airportIds[0]").value(airportId));

        mockMvc.perform(get("/api/airports/{id}", airportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Denver International"));

        mockMvc.perform(get("/api/passengers/{id}", passengerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aircraftIds[0]").value(aircraftId));

        mockMvc.perform(put("/api/aircraft/{id}", aircraftId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"737-800","airlineName":"Example Air","numberOfPassengers":180,"passengerIds":[%d],"airportIds":[%d]}
                                """.formatted(passengerId, airportId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("737-800"))
                .andExpect(jsonPath("$.passengerIds[0]").value(passengerId))
                .andExpect(jsonPath("$.airportIds[0]").value(airportId));

        mockMvc.perform(delete("/api/aircraft/{id}", aircraftId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/airports/{id}", airportId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/passengers/{id}", passengerId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/cities/{id}", secondCityId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/cities/{id}", cityId))
                .andExpect(status().isNoContent());
    }
}
