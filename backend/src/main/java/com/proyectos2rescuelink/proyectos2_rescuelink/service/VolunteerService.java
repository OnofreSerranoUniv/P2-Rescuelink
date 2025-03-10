package com.proyectos2rescuelink.proyectos2_rescuelink.service;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.User;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.Volunteer;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.UserRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;

    public VolunteerService(VolunteerRepository volunteerRepository, UserRepository userRepository) {
        this.volunteerRepository = volunteerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Volunteer registerVolunteer(Long userId, String fullName, String phoneNumber, String location, String skills) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        if (volunteerRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("El usuario ya está registrado como voluntario.");
        }

        Volunteer volunteer = new Volunteer();
        volunteer.setUser(user.get());
        volunteer.setFullName(fullName);
        volunteer.setPhoneNumber(phoneNumber);
        volunteer.setLocation(location);
        volunteer.setSkills(skills);
        volunteer.setAvailable(true);

        return volunteerRepository.save(volunteer);
    }

    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    public Optional<Volunteer> getVolunteerByUserId(Long userId) {
        return volunteerRepository.findByUserId(userId);
    }
}
