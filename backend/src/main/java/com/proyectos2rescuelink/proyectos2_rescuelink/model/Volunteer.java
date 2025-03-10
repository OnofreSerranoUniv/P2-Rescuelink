package com.proyectos2rescuelink.proyectos2_rescuelink.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "volunteers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String skills; // Ejemplo: "Primeros auxilios, Rescate en montaña"

    @Column(nullable = false)
    private boolean available; // Si el voluntario
}