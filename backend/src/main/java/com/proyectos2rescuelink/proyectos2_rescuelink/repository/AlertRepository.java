package com.proyectos2rescuelink.proyectos2_rescuelink.repository;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByActive(boolean active);  // 🔹 Nuevo método
}
