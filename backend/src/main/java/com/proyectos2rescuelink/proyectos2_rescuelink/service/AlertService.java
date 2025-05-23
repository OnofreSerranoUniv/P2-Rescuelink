package com.proyectos2rescuelink.proyectos2_rescuelink.service;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Alert;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.User;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.Volunteer;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.VolunteerAlert;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.AlertRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.UserRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerAlertRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.util.GeolocationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final VolunteerAlertRepository volunteerAlertRepository; // ✅ FALTABA ESTO

    public AlertService(
            AlertRepository alertRepository,
            UserRepository userRepository,
            VolunteerRepository volunteerRepository,
            VolunteerAlertRepository volunteerAlertRepository // ✅ AÑADIR EN EL CONSTRUCTOR
    ) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.volunteerRepository = volunteerRepository;
        this.volunteerAlertRepository = volunteerAlertRepository; // ✅ INYECTAR
    }

    public Alert createAlert(String title, String description, String location, String alertType) {
        Alert alert = new Alert();
        alert.setTitle(title);
        alert.setDescription(description);
        alert.setLocation(location);
        alert.setAlertType(alertType);
        alert.setActive(true);
        alert.setTimestamp(LocalDateTime.now());

        // Coordenadas a partir de la ubicación
        double[] coords = GeolocationService.getCoordinates(location);
        alert.setLatitude(coords[0]);
        alert.setLongitude(coords[1]);

        return alertRepository.save(alert);
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByActive(true);
    }

    public Optional<Alert> getAlertById(Long id) {
        return alertRepository.findById(id);
    }

    public Alert updateAlertStatus(Long id, boolean active) {
        Alert alert = alertRepository.findById(id).orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        alert.setActive(active);
        return alertRepository.save(alert);
    }

    @Transactional
    public boolean joinAlert(String userEmail, Long alertId) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) throw new RuntimeException("Usuario no encontrado");

        User user = userOpt.get();
        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(user.getId());
        if (volunteerOpt.isEmpty()) throw new RuntimeException("Debes tener un perfil de voluntario para unirte");

        Volunteer volunteer = volunteerOpt.get();

        // Verificar si ya está unido a la alerta
        boolean yaInscrito = volunteerAlertRepository.findByVolunteer(volunteer)
                .stream()
                .anyMatch(va -> va.getAlert().getId().equals(alertId));

        if (yaInscrito) return false;

        // Crear nueva relación
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        VolunteerAlert va = new VolunteerAlert();
        va.setVolunteer(volunteer);
        va.setAlert(alert);
        va.setActive(true);

        volunteerAlertRepository.save(va);
        return true;
    }
}
