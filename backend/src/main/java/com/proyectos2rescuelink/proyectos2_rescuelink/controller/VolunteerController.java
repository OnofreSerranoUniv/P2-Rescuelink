package com.proyectos2rescuelink.proyectos2_rescuelink.controller;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Volunteer;
import com.proyectos2rescuelink.proyectos2_rescuelink.service.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {
    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerVolunteer(@RequestBody Map<String, String> request) {
        try {
            Long userId = Long.parseLong(request.get("userId"));
            String fullName = request.get("fullName");
            String phoneNumber = request.get("phoneNumber");
            String location = request.get("location");
            String skills = request.get("skills");

            Volunteer volunteer = volunteerService.registerVolunteer(userId, fullName, phoneNumber, location, skills);
            return ResponseEntity.ok(Map.of("message", "Voluntario registrado con éxito", "volunteerId", volunteer.getId()));
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
}
