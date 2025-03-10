package com.proyectos2rescuelink.proyectos2_rescuelink.service;

import com.proyectos2rescuelink.proyectos2_rescuelink.model.Role;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.RoleType;
import com.proyectos2rescuelink.proyectos2_rescuelink.model.User;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.RoleRepository;
import com.proyectos2rescuelink.proyectos2_rescuelink.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User registerUser(String username, String email, String password, boolean isVolunteer) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        // Determinar el rol basado en si el usuario es voluntario o no
        RoleType roleType = isVolunteer ? RoleType.VOLUNTEER : RoleType.USER;

        // Buscar o crear el rol correspondiente
        Role defaultRole = roleRepository.findByName(roleType)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleType); // 🟢 Se asigna directamente el enum RoleType
                    return roleRepository.save(newRole);
                });

        // Crear nuevo usuario
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRoles(Set.of(defaultRole));
        newUser.setVolunteer(isVolunteer);

        return userRepository.save(newUser);
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }

    public Optional<User> authenticateByEmail(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
