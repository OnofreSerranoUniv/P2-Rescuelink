package com.proyectos2recuelink.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.bind.annotation.*;

@RestController
public class LocationEndpoint {

    @GetMapping("/set-location")
    public void setLocation(@RequestParam double lat, @RequestParam double lon,
                            HttpServletRequest request, HttpServletResponse response) {
        VaadinSession.getCurrent().setAttribute("latitude", lat);
        VaadinSession.getCurrent().setAttribute("longitude", lon);
        System.out.println("📍 Ubicación guardada: " + lat + ", " + lon);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
