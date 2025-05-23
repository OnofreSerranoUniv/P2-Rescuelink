package com.proyectos2rescuelink.proyectos2_rescuelink.controller;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.User;
import com.proyectos2rescuelink.proyectos2_rescuelink.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// ✅ Endpoint para actualizar voluntariado y ubicación
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/me/volunteer")
    public ResponseEntity<?> updateVolunteerStatus(
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request
    ) {
        try {
            String email = (String) request.getAttribute("userEmail"); // ← viene del JwtFilter

            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no válido o expirado"));
            }

            boolean isVolunteer = Boolean.parseBoolean(payload.get("isVolunteer").toString());
            Double latitude = payload.get("latitude") != null ? Double.parseDouble(payload.get("latitude").toString()) : null;
            Double longitude = payload.get("longitude") != null ? Double.parseDouble(payload.get("longitude").toString()) : null;

            User updated = userService.updateVolunteerStatus(email, isVolunteer, latitude, longitude);

            return ResponseEntity.ok(Map.of(
                    "message", "Voluntariado actualizado",
                    "volunteer", updated.isVolunteer()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // (Opcional) Obtener el usuario actual
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        return userService.findByEmail(principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/me/test")
    public ResponseEntity<String> testPrincipal(Principal principal) {
        return ResponseEntity.ok("Estás autenticado como: " + principal.getName());
    }

}
