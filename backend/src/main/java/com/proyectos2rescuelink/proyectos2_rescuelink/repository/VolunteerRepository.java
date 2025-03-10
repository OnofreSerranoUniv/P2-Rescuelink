package com.proyectos2rescuelink.proyectos2_rescuelink.repository;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUserId(Long userId);
}
