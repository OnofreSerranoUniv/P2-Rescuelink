package com.proyectos2rescuelink.proyectos2_rescuelink.controller;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Alert;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.VolunteerAlert;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.User;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.Volunteer;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.UserRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerAlertRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.service.VolunteerAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/volunteer-alerts")
public class VolunteerAlertController {

    private final VolunteerAlertService volunteerAlertService;
    private final VolunteerRepository volunteerRepository;
    private final VolunteerAlertRepository volunteerAlertRepository;
    private final UserRepository userRepository;

    public VolunteerAlertController(
            VolunteerAlertService volunteerAlertService,
            VolunteerRepository volunteerRepository,
            VolunteerAlertRepository volunteerAlertRepository,
            UserRepository userRepository
    ) {
        this.volunteerAlertService = volunteerAlertService;
        this.volunteerRepository = volunteerRepository;
        this.volunteerAlertRepository = volunteerAlertRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinAlert(@RequestBody JoinRequest request) {
        volunteerAlertService.joinAlert(request.volunteerId, request.alertId);
        return ResponseEntity.ok("Voluntario unido a la alerta.");
    }

    @GetMapping("/{volunteerId}")
    public ResponseEntity<List<Alert>> getAlertsForVolunteer(@PathVariable Long volunteerId) {
        List<VolunteerAlert> vas = volunteerAlertService.getAlertsForVolunteer(volunteerId);
        List<Alert> alerts = vas.stream()
                .map(VolunteerAlert::getAlert)
                .toList();
        return ResponseEntity.ok(alerts);
    }


    @GetMapping("/me")
    public ResponseEntity<?> getMyVolunteerAlerts(Principal principal) {
        String email = principal.getName();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("Usuario no encontrado");

        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userOpt.get().getId());
        if (volunteerOpt.isEmpty()) return ResponseEntity.status(404).body("No tienes perfil de voluntario");

        List<VolunteerAlert> alerts = volunteerAlertRepository.findByVolunteer(volunteerOpt.get());
        return ResponseEntity.ok(alerts.stream().map(VolunteerAlert::getAlert).toList());
    }

    public record JoinRequest(Long volunteerId, Long alertId) {}
}
