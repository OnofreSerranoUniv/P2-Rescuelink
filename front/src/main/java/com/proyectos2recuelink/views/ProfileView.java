package com.proyectos2recuelink.views;

import com.proyectos2recuelink.security.SecurityUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Route(value = "profile", layout = MainLayout.class)
@CssImport("styles/profile.css")
public class ProfileView extends VerticalLayout {

    public ProfileView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        addClassName("profile-view");

        JsonObject profile = fetchProfile();

        if (profile != null) {
            String username = profile.getString("username");
            String email = profile.getString("email");
            boolean isVolunteer = profile.getBoolean("volunteer");

            Avatar avatar = new Avatar(username);
            avatar.setWidth("100px");
            avatar.setHeight("100px");

            H2 nameLabel = new H2(username);
            Span emailLabel = new Span(email);
            Span roleLabel = new Span(isVolunteer ? "✅ Voluntario" : "👤 Usuario normal");
            roleLabel.addClassName("user-status");

            add(avatar, nameLabel, emailLabel, roleLabel);
        } else {
            Notification.show("No se pudo cargar el perfil. Redirigiendo...", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("login");
        }
    }

    private JsonObject fetchProfile() {
        try {
            String token = SecurityUtils.getToken();
            if (token == null) return null;

            URL url = new URL("http://localhost:8081/api/auth/profile");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) return null;

            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonValue parsed = JsonUtil.parse(response);
            if (parsed instanceof JsonObject json) return json;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
