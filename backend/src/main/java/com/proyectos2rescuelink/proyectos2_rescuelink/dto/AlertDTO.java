package com.proyectos2rescuelink.proyectos2_rescuelink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String alertType;
    private boolean active;
    private String formattedDate;
}
