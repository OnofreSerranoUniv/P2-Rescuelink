package com.proyectos2rescuelink.proyectos2_rescuelink.repository;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.VolunteerAlert;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VolunteerAlertRepository extends JpaRepository<VolunteerAlert, Long> {
    List<VolunteerAlert> findByVolunteer(Volunteer volunteer);
}
