package com.example.sprintapi.api;

import com.example.sprintapi.api.ApiDtos.*;
import com.example.sprintapi.service.DirectoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DirectoryController {
    private final DirectoryService service;

    public DirectoryController(DirectoryService service) {
        this.service = service;
    }

    @GetMapping("/cities")
    public PageResponse<CityResponse> cities(@PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)
                                             Pageable pageable) {
        return service.cities(pageable);
    }

    @GetMapping("/cities/{id}")
    public CityResponse city(@PathVariable Long id) { return service.city(id); }

    @PostMapping("/cities")
    @ResponseStatus(HttpStatus.CREATED)
    public CityResponse createCity(@Valid @RequestBody CityRequest request) { return service.createCity(request); }

    @PutMapping("/cities/{id}")
    public CityResponse updateCity(@PathVariable Long id, @Valid @RequestBody CityRequest request) {
        return service.updateCity(id, request);
    }

    @DeleteMapping("/cities/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Long id) { service.deleteCity(id); }

    @GetMapping("/airports")
    public List<AirportResponse> airports() { return service.allAirports(); }

    @GetMapping("/airports/{id}")
    public AirportResponse airport(@PathVariable Long id) { return service.airport(id); }

    @PostMapping("/airports")
    @ResponseStatus(HttpStatus.CREATED)
    public AirportResponse createAirport(@Valid @RequestBody AirportRequest request) {
        return service.createAirport(request);
    }

    @PutMapping("/airports/{id}")
    public AirportResponse updateAirport(@PathVariable Long id, @Valid @RequestBody AirportRequest request) {
        return service.updateAirport(id, request);
    }

    @DeleteMapping("/airports/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAirport(@PathVariable Long id) { service.deleteAirport(id); }

    @GetMapping("/passengers")
    public List<PassengerResponse> passengers() { return service.allPassengers(); }

    @GetMapping("/passengers/{id}")
    public PassengerResponse passenger(@PathVariable Long id) { return service.passenger(id); }

    @PostMapping("/passengers")
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponse createPassenger(@Valid @RequestBody PassengerRequest request) {
        return service.createPassenger(request);
    }

    @PutMapping("/passengers/{id}")
    public PassengerResponse updatePassenger(@PathVariable Long id, @Valid @RequestBody PassengerRequest request) {
        return service.updatePassenger(id, request);
    }

    @DeleteMapping("/passengers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable Long id) { service.deletePassenger(id); }

    @GetMapping("/aircraft")
    public List<AircraftResponse> aircraft() { return service.allAircraft(); }

    @GetMapping("/aircraft/{id}")
    public AircraftResponse aircraft(@PathVariable Long id) { return service.aircraft(id); }

    @PostMapping("/aircraft")
    @ResponseStatus(HttpStatus.CREATED)
    public AircraftResponse createAircraft(@Valid @RequestBody AircraftRequest request) {
        return service.createAircraft(request);
    }

    @PutMapping("/aircraft/{id}")
    public AircraftResponse updateAircraft(@PathVariable Long id, @Valid @RequestBody AircraftRequest request) {
        return service.updateAircraft(id, request);
    }

    @DeleteMapping("/aircraft/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAircraft(@PathVariable Long id) { service.deleteAircraft(id); }
}
