package com.proyectos2rescuelink.proyectos2_rescuelink.controller;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Alert;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.User;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.Volunteer;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.VolunteerAlert;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.AlertRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.UserRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerAlertRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.service.UserService;
import com.proyectos2rescuelink.proyectos2_rescuelink.service.VolunteerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final UserService userService;
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final AlertRepository alertRepository;
    private final VolunteerAlertRepository volunteerAlertRepository; // ✅ FALTABA ESTO

    public VolunteerController(
            VolunteerService volunteerService,
            UserService userService,
            VolunteerRepository volunteerRepository,
            UserRepository userRepository,
            AlertRepository alertRepository,
            VolunteerAlertRepository volunteerAlertRepository // ✅ AÑADIDO
    ) {
        this.volunteerService = volunteerService;
        this.userService = userService;
        this.volunteerRepository = volunteerRepository;
        this.userRepository = userRepository;
        this.alertRepository = alertRepository;
        this.volunteerAlertRepository = volunteerAlertRepository; // ✅ GUARDARLO
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerVolunteer(@RequestBody Map<String, String> payload, Principal principal) {
        try {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no encontrado"));
            }

            User user = userOpt.get();

            String fullName = payload.get("fullName");
            String phone = payload.get("phoneNumber");
            String location = payload.get("location");
            String skills = payload.get("skills");

            Volunteer volunteer = volunteerService.registerVolunteer(user.getId(), fullName, phone, location, skills);
            return ResponseEntity.ok(Map.of("message", "Voluntario registrado", "volunteerId", volunteer.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Volunteer>> getAllVolunteers() {
        return ResponseEntity.ok(volunteerService.getAllVolunteers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getVolunteerByUserId(@PathVariable Long userId) {
        Optional<Volunteer> volunteer = volunteerService.getVolunteerByUserId(userId);
        return volunteer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyVolunteerProfile(Principal principal) {
        String email = principal.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        User user = userOpt.get();
        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(user.getId());

        boolean hasProfile = volunteerOpt.isPresent();

        return ResponseEntity.ok(Map.of(
                "isVolunteer", user.isVolunteer(),
                "hasProfile", hasProfile
        ));
    }

    @GetMapping("/{id}/alerts")
    public ResponseEntity<?> getAlertsForVolunteer(@PathVariable Long id) {
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(id);
        if (volunteerOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Voluntario no encontrado"));
        }

        Volunteer volunteer = volunteerOpt.get();
        List<VolunteerAlert> vas = volunteerAlertRepository.findByVolunteer(volunteer);
        List<Alert> alerts = vas.stream().map(VolunteerAlert::getAlert).toList();

        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/me/alerts")
    public ResponseEntity<?> getMyAlerts(Principal principal) {
        String email = principal.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("Usuario no encontrado");

        Optional<Volunteer> volunteerOpt = volunteerRepository.findByUserId(userOpt.get().getId());
        if (volunteerOpt.isEmpty()) return ResponseEntity.ok(List.of());

        List<VolunteerAlert> vas = volunteerAlertRepository.findByVolunteer(volunteerOpt.get());
        return ResponseEntity.ok(vas.stream().map(VolunteerAlert::getAlert).toList());
    }
}
