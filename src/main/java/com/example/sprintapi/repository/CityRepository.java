package com.example.sprintapi.repository;

import com.example.sprintapi.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
