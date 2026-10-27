package com.example.sprintapi.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record CityRequest(
            @NotBlank String name,
            @NotBlank String state,
            @Min(0) long population
    ) { }

    public record CityResponse(
            Long id,
            String name,
            String state,
            long population,
            Set<Long> airportIds,
            Set<Long> passengerIds
    ) { }

    public record PageResponse<T>(
            List<T> content,
            int number,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) { }

    public record AirportRequest(
            @NotBlank String name,
            @NotBlank String code,
            @NotNull Long cityId,
            @NotNull List<Long> aircraftIds
    ) { }

    public record AirportResponse(
            Long id,
            String name,
            String code,
            Long cityId,
            Set<Long> aircraftIds
    ) { }

    public record PassengerRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String phoneNumber,
            @NotNull Long cityId,
            @NotNull List<Long> aircraftIds
    ) { }

    public record PassengerResponse(
            Long id,
            String firstName,
            String lastName,
            String phoneNumber,
            Long cityId,
            Set<Long> aircraftIds
    ) { }

    public record AircraftRequest(
            @NotBlank String type,
            @NotBlank String airlineName,
            @Min(0) int numberOfPassengers,
            @NotNull List<Long> passengerIds,
            @NotNull List<Long> airportIds
    ) { }

    public record AircraftResponse(
            Long id,
            String type,
            String airlineName,
            int numberOfPassengers,
            Set<Long> passengerIds,
            Set<Long> airportIds
    ) { }
}
