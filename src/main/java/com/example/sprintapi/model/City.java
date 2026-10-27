package com.example.sprintapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private long population;

    @JsonIgnore
    @OneToMany(mappedBy = "city")
    private Set<Airport> airports = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "city")
    private Set<Passenger> passengers = new HashSet<>();

    protected City() {
    }

    public City(String name, String state, long population) {
        this.name = name;
        this.state = state;
        this.population = population;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public long getPopulation() { return population; }
    public void setPopulation(long population) { this.population = population; }
    public Set<Airport> getAirports() { return airports; }
    public Set<Passenger> getPassengers() { return passengers; }
}
