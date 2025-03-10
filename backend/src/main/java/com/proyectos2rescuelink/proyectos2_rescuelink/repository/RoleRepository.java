package com.proyectos2rescuelink.proyectos2_rescuelink.repository;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Role;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name); // 🟢 Cambiamos String por RoleType
}
