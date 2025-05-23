package com.proyectos2rescuelink.proyectos2_rescuelink.controller;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Alert;
import com.proyectos2rescuelink.proyectos2_rescuelink.dto.AlertDTO;
import com.proyectos2rescuelink.proyectos2_rescuelink.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAlert(@RequestBody Map<String, String> request) {
        System.out.println("🔍 Datos recibidos en el backend: " + request);

        try {
            String title = request.get("title");
            String description = request.get("description");
            String location = request.get("location");
            String alertType = request.getOrDefault("alertType", "general");

            // Validar que todos los campos estén presentes
            if (title == null || title.isEmpty() ||
                    description == null || description.isEmpty() ||
                    location == null || location.isEmpty() ||
                    alertType == null || alertType.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Todos los campos son obligatorios"));
            }

            // Crear la alerta sin imagen
            Alert alert = alertService.createAlert(title, description, location, alertType);

            return ResponseEntity.ok(Map.of("message", "Alerta creada con éxito", "alertId", alert.getId()));

        } catch (Exception e) {
            System.out.println("❌ Error en el servidor: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy, HH:mm'h'")
                .withLocale(new Locale("es", "ES"));

        List<AlertDTO> alertDTOs = alertService.getAllAlerts().stream().map(alert -> new AlertDTO(
                alert.getId(),
                alert.getTitle(),
                alert.getDescription(),
                alert.getLocation(),
                alert.getAlertType(),
                alert.isActive(),
                alert.getTimestamp().format(formatter)
        )).collect(Collectors.toList());

        return ResponseEntity.ok(alertDTOs);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Alert>> getActiveAlerts() {
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlertById(@PathVariable Long id) {
        Optional<Alert> alert = alertService.getAlertById(id);
        return alert.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateAlertStatus(@PathVariable Long id, @RequestParam boolean active) {
        try {
            Alert alert = alertService.updateAlertStatus(id, active);
            return ResponseEntity.ok(Map.of("message", "Estado de la alerta actualizado", "active", alert.isActive()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinAlert(@PathVariable Long id, Principal principal) {
        try {
            String email = principal.getName();

            Optional<Alert> alertOpt = alertService.getAlertById(id);
            if (alertOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Alerta no encontrada"));
            }

            boolean joined = alertService.joinAlert(email, id);

            if (joined) {
                return ResponseEntity.ok(Map.of("message", "Te has unido a la alerta correctamente"));
            } else {
                return ResponseEntity.status(409).body(Map.of("error", "Ya estás unido a esta alerta"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
