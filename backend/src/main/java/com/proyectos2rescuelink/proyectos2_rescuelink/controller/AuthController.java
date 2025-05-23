package com.proyectos2rescuelink.proyectos2_rescuelink.controller;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.User;
import com.proyectos2rescuelink.proyectos2_rescuelink.security.JwtUtil;
import com.proyectos2rescuelink.proyectos2_rescuelink.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.proyectos2rescuelink.proyectos2_rescuelink.service.SessionService;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final SessionService sessionService;

    public AuthController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        boolean isVolunteer = request.containsKey("isVolunteer") && Boolean.TRUE.equals(request.get("isVolunteer"));

        try {
            User newUser = userService.registerUser(username, email, password, isVolunteer);
            return ResponseEntity.ok(Map.of("message", "Usuario registrado con éxito", "user", newUser.getUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> user = userService.authenticateByEmail(email, password);
        if (user.isPresent()) {
            String token = JwtUtil.generateToken(email);
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }
    }


    @GetMapping("/get-username")
    public ResponseEntity<Map<String, String>> getUsername(@RequestParam String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of("username", user.get().getUsername()));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
        }

        String email = principal.getName(); // ← funciona gracias al JwtFilter actualizado
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado"));
        }

        User user = userOpt.get();

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "volunteer", user.isVolunteer()
        ));
    }




}
