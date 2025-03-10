package com.proyectos2rescuelink.proyectos2_rescuelink.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private static final String USER_SESSION_KEY = "loggedUser";
    private final HttpSession httpSession;

    public SessionService(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void loginUser(String username) {
        httpSession.setAttribute(USER_SESSION_KEY, username);
    }

    public void logoutUser() {
        httpSession.removeAttribute(USER_SESSION_KEY);
        httpSession.invalidate();
    }

    public String getLoggedUser() {
        return (String) httpSession.getAttribute(USER_SESSION_KEY);
    }

    public boolean isUserLoggedIn() {
        return getLoggedUser() != null;
    }
}
