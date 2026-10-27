package com.example.sprintapi.service;

import com.example.sprintapi.api.ApiDtos.*;
import com.example.sprintapi.model.*;
import com.example.sprintapi.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DirectoryService {
    private final CityRepository cities;
    private final AirportRepository airports;
    private final PassengerRepository passengers;
    private final AircraftRepository aircraft;

    public DirectoryService(CityRepository cities, AirportRepository airports,
                            PassengerRepository passengers, AircraftRepository aircraft) {
        this.cities = cities;
        this.airports = airports;
        this.passengers = passengers;
        this.aircraft = aircraft;
    }

    @Transactional(readOnly = true)
    public PageResponse<CityResponse> cities(Pageable pageable) {
        Page<CityResponse> page = cities.findAll(pageable).map(this::cityResponse);
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isFirst(), page.isLast());
    }

    @Transactional(readOnly = true)
    public CityResponse city(Long id) {
        return cityResponse(require(cities, id, "City"));
    }

    public CityResponse createCity(CityRequest request) {
        return cityResponse(cities.save(new City(request.name(), request.state(), request.population())));
    }

    public CityResponse updateCity(Long id, CityRequest request) {
        City city = require(cities, id, "City");
        city.setName(request.name());
        city.setState(request.state());
        city.setPopulation(request.population());
        return cityResponse(cities.save(city));
    }

    public void deleteCity(Long id) {
        cities.delete(require(cities, id, "City"));
    }

    @Transactional(readOnly = true)
    public List<AirportResponse> allAirports() {
        return airports.findAll().stream().map(this::airportResponse).toList();
    }

    @Transactional(readOnly = true)
    public AirportResponse airport(Long id) {
        return airportResponse(require(airports, id, "Airport"));
    }

    public AirportResponse createAirport(AirportRequest request) {
        Airport airport = new Airport(request.name(), request.code(), require(cities, request.cityId(), "City"));
        for (Long aircraftId : request.aircraftIds()) {
            Aircraft plane = require(aircraft, aircraftId, "Aircraft");
            plane.getAirports().add(airport);
            airport.getAircraft().add(plane);
        }
        return airportResponse(airports.save(airport));
    }

    public AirportResponse updateAirport(Long id, AirportRequest request) {
        Airport airport = require(airports, id, "Airport");
        airport.setName(request.name());
        airport.setCode(request.code());
        airport.setCity(require(cities, request.cityId(), "City"));
        airport.getAircraft().forEach(plane -> plane.getAirports().remove(airport));
        airport.getAircraft().clear();
        for (Long aircraftId : request.aircraftIds()) {
            Aircraft plane = require(aircraft, aircraftId, "Aircraft");
            plane.getAirports().add(airport);
            airport.getAircraft().add(plane);
        }
        return airportResponse(airports.save(airport));
    }

    public void deleteAirport(Long id) {
        Airport airport = require(airports, id, "Airport");
        airport.getAircraft().forEach(plane -> plane.getAirports().remove(airport));
        airports.delete(airport);
    }

    @Transactional(readOnly = true)
    public List<PassengerResponse> allPassengers() {
        return passengers.findAll().stream().map(this::passengerResponse).toList();
    }

    @Transactional(readOnly = true)
    public PassengerResponse passenger(Long id) {
        return passengerResponse(require(passengers, id, "Passenger"));
    }

    public PassengerResponse createPassenger(PassengerRequest request) {
        Passenger passenger = new Passenger(request.firstName(), request.lastName(), request.phoneNumber(),
                require(cities, request.cityId(), "City"));
        setIds(passenger.getAircraft(), request.aircraftIds(), aircraft, "Aircraft");
        passenger.getAircraft().forEach(plane -> plane.getPassengers().add(passenger));
        return passengerResponse(passengers.save(passenger));
    }

    public PassengerResponse updatePassenger(Long id, PassengerRequest request) {
        Passenger passenger = require(passengers, id, "Passenger");
        passenger.setFirstName(request.firstName());
        passenger.setLastName(request.lastName());
        passenger.setPhoneNumber(request.phoneNumber());
        passenger.setCity(require(cities, request.cityId(), "City"));
        passenger.getAircraft().forEach(plane -> plane.getPassengers().remove(passenger));
        replaceIds(passenger.getAircraft(), request.aircraftIds(), aircraft, "Aircraft");
        passenger.getAircraft().forEach(plane -> plane.getPassengers().add(passenger));
        return passengerResponse(passengers.save(passenger));
    }

    public void deletePassenger(Long id) {
        Passenger passenger = require(passengers, id, "Passenger");
        passenger.getAircraft().forEach(plane -> plane.getPassengers().remove(passenger));
        passengers.delete(passenger);
    }

    @Transactional(readOnly = true)
    public List<AircraftResponse> allAircraft() {
        return aircraft.findAll().stream().map(this::aircraftResponse).toList();
    }

    @Transactional(readOnly = true)
    public AircraftResponse aircraft(Long id) {
        return aircraftResponse(require(aircraft, id, "Aircraft"));
    }

    public AircraftResponse createAircraft(AircraftRequest request) {
        Aircraft plane = new Aircraft(request.type(), request.airlineName(), request.numberOfPassengers());
        for (Long passengerId : request.passengerIds()) {
            Passenger passenger = require(passengers, passengerId, "Passenger");
            passenger.getAircraft().add(plane);
            plane.getPassengers().add(passenger);
        }
        for (Long airportId : request.airportIds()) {
            Airport airport = require(airports, airportId, "Airport");
            plane.getAirports().add(airport);
            airport.getAircraft().add(plane);
        }
        return aircraftResponse(aircraft.save(plane));
    }

    public AircraftResponse updateAircraft(Long id, AircraftRequest request) {
        Aircraft plane = require(aircraft, id, "Aircraft");
        plane.setType(request.type());
        plane.setAirlineName(request.airlineName());
        plane.setNumberOfPassengers(request.numberOfPassengers());
        plane.getPassengers().forEach(passenger -> passenger.getAircraft().remove(plane));
        plane.getPassengers().clear();
        for (Long passengerId : request.passengerIds()) {
            Passenger passenger = require(passengers, passengerId, "Passenger");
            passenger.getAircraft().add(plane);
            plane.getPassengers().add(passenger);
        }
        plane.getAirports().forEach(airport -> airport.getAircraft().remove(plane));
        plane.getAirports().clear();
        for (Long airportId : request.airportIds()) {
            Airport airport = require(airports, airportId, "Airport");
            plane.getAirports().add(airport);
            airport.getAircraft().add(plane);
        }
        return aircraftResponse(aircraft.save(plane));
    }

    public void deleteAircraft(Long id) {
        Aircraft plane = require(aircraft, id, "Aircraft");
        plane.getPassengers().forEach(passenger -> passenger.getAircraft().remove(plane));
        plane.getAirports().forEach(airport -> airport.getAircraft().remove(plane));
        aircraft.delete(plane);
    }

    private CityResponse cityResponse(City city) {
        return new CityResponse(city.getId(), city.getName(), city.getState(), city.getPopulation(),
                ids(city.getAirports(), Airport::getId), ids(city.getPassengers(), Passenger::getId));
    }

    private AirportResponse airportResponse(Airport airport) {
        return new AirportResponse(airport.getId(), airport.getName(), airport.getCode(),
                airport.getCity().getId(), ids(airport.getAircraft(), Aircraft::getId));
    }

    private PassengerResponse passengerResponse(Passenger passenger) {
        return new PassengerResponse(passenger.getId(), passenger.getFirstName(), passenger.getLastName(),
                passenger.getPhoneNumber(), passenger.getCity().getId(), ids(passenger.getAircraft(), Aircraft::getId));
    }

    private AircraftResponse aircraftResponse(Aircraft plane) {
        return new AircraftResponse(plane.getId(), plane.getType(), plane.getAirlineName(),
                plane.getNumberOfPassengers(), ids(plane.getPassengers(), Passenger::getId),
                ids(plane.getAirports(), Airport::getId));
    }

    private <T> Set<Long> ids(Iterable<T> entities, java.util.function.Function<T, Long> idExtractor) {
        Set<Long> ids = new HashSet<>();
        entities.forEach(entity -> ids.add(idExtractor.apply(entity)));
        return ids;
    }

    private <T, ID> T require(org.springframework.data.jpa.repository.JpaRepository<T, ID> repository,
                              ID id, String label) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, label + " " + id + " was not found"));
    }

    private <T> void setIds(Set<T> target, List<Long> ids,
                            org.springframework.data.jpa.repository.JpaRepository<T, Long> repository,
                            String label) {
        target.addAll(ids.stream().map(id -> require(repository, id, label)).collect(Collectors.toSet()));
    }

    private <T> void replaceIds(Set<T> target, List<Long> ids,
                                org.springframework.data.jpa.repository.JpaRepository<T, Long> repository,
                                String label) {
        target.clear();
        setIds(target, ids, repository, label);
    }
}
