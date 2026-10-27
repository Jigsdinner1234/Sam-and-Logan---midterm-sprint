package com.example.sprintapi.repository;

import com.example.sprintapi.model.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
}
