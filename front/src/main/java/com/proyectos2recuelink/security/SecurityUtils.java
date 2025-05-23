package com.proyectos2recuelink.security;

import com.vaadin.flow.server.VaadinSession;

import java.net.HttpURLConnection;
import java.util.Optional;

public class SecurityUtils {

    public static Optional<String> getUserEmail() {
        return Optional.ofNullable((String) VaadinSession.getCurrent().getAttribute("user-email"));
    }

    public static Optional<String> getUsername() {
        return Optional.ofNullable((String) VaadinSession.getCurrent().getAttribute("username"));
    }


    public static boolean isAuthenticated() {
        boolean authenticated = getUserEmail().isPresent();
        System.out.println("¿Usuario autenticado?: " + authenticated);
        return authenticated;
    }

    public static String getToken() {
        return (String) VaadinSession.getCurrent().getAttribute("jwt-token");
    }

    public static void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
    }

    public static void addAuthHeader(HttpURLConnection conn) {
        String token = getToken();
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
    }

}
