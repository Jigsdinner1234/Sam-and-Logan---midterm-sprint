package com.example.sprintapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aircraft_type", nullable = false)
    private String type;

    @Column(nullable = false)
    private String airlineName;

    @Column(nullable = false)
    private int numberOfPassengers;

    @JsonIgnore
    @ManyToMany(mappedBy = "aircraft")
    private Set<Passenger> passengers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "aircraft_airport",
            joinColumns = @JoinColumn(name = "aircraft_id"),
            inverseJoinColumns = @JoinColumn(name = "airport_id")
    )
    private Set<Airport> airports = new HashSet<>();

    protected Aircraft() {
    }

    public Aircraft(String type, String airlineName, int numberOfPassengers) {
        this.type = type;
        this.airlineName = airlineName;
        this.numberOfPassengers = numberOfPassengers;
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }
    public int getNumberOfPassengers() { return numberOfPassengers; }
    public void setNumberOfPassengers(int numberOfPassengers) { this.numberOfPassengers = numberOfPassengers; }
    public Set<Passenger> getPassengers() { return passengers; }
    public Set<Airport> getAirports() { return airports; }
}
