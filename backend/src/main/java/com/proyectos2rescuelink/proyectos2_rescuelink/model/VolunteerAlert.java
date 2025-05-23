package com.proyectos2rescuelink.proyectos2_rescuelink.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "volunteer_alerts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VolunteerAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;

    @ManyToOne
    @JoinColumn(name = "alert_id")
    private Alert alert;

    private boolean active = true; // si sigue participando en la alerta
}
