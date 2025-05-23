package com.proyectos2rescuelink.proyectos2_rescuelink.service;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Alert;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.Volunteer;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.VolunteerAlert;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.AlertRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerAlertRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerAlertService {

    private final VolunteerAlertRepository volunteerAlertRepository;
    private final AlertRepository alertRepository;
    private final VolunteerRepository volunteerRepository;

    public VolunteerAlertService(VolunteerAlertRepository volunteerAlertRepository, AlertRepository alertRepository, VolunteerRepository volunteerRepository) {
        this.volunteerAlertRepository = volunteerAlertRepository;
        this.alertRepository = alertRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public void joinAlert(Long volunteerId, Long alertId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId).orElseThrow();
        Alert alert = alertRepository.findById(alertId).orElseThrow();

        VolunteerAlert va = new VolunteerAlert();
        va.setVolunteer(volunteer);
        va.setAlert(alert);

        volunteerAlertRepository.save(va);
    }

    public List<VolunteerAlert> getAlertsForVolunteer(Long volunteerId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId).orElseThrow();
        return volunteerAlertRepository.findByVolunteer(volunteer);
    }
}
