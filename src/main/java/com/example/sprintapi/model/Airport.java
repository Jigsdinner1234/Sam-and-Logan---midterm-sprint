package com.example.sprintapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @JsonIgnore
    @ManyToMany(mappedBy = "airports")
    private final Set<Aircraft> aircraft = new HashSet<>();

    protected Airport() {
    }

    public Airport(String name, String code, City city) {
        this.name = name;
        this.code = code;
        this.city = city;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public Set<Aircraft> getAircraft() { return aircraft; }
}
