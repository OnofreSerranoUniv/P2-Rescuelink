package com.proyectos2recuelink.views;

import com.proyectos2recuelink.security.SecurityUtils;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;
import com.vaadin.flow.component.dialog.Dialog;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Route(value = "volunteers", layout = MainLayout.class)
@CssImport("styles/volunteers.css")
public class VolunteersView extends VerticalLayout {

    private final VerticalLayout contentLayout = new VerticalLayout();
    private final FlexLayout volunteerContainer = new FlexLayout();

    public VolunteersView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        addClassName("volunteers-view");

        volunteerContainer.setClassName("volunteer-container");
        volunteerContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        volunteerContainer.setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1("Voluntarios"), contentLayout);

        comprobarEstadoVoluntario();
    }

    private void comprobarEstadoVoluntario() {
        try {
            URL url = new URL("http://localhost:8081/api/volunteers/me");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            SecurityUtils.addAuthHeader(conn);

            if (conn.getResponseCode() != 200) {
                Notification.show("Error: No autorizado");
                return;
            }

            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonObject json = (JsonObject) JsonUtil.parse(response);
            boolean isVolunteer = json.getBoolean("isVolunteer");
            boolean hasProfile = json.getBoolean("hasProfile");

            if (!isVolunteer || hasProfile) {
                showVolunteersListView();
            } else {
                showVolunteerProfileForm();
            }

        } catch (Exception e) {
            Notification.show("Error al verificar estado: " + e.getMessage());
        }
    }

    private void showVolunteersListView() {
        contentLayout.removeAll();
        loadVolunteers();
        contentLayout.add(volunteerContainer);
    }

    private void loadVolunteers() {
        try {
            URL url = new URL("http://localhost:8081/api/volunteers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            SecurityUtils.addAuthHeader(conn);
            conn.setRequestProperty("Accept", "application/json");

            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonArray array = (JsonArray) JsonUtil.parse(response);
            volunteerContainer.removeAll();

            for (int i = 0; i < array.length(); i++) {
                JsonObject obj = array.getObject(i);
                volunteerContainer.add(createVolunteerCard(obj));
            }

        } catch (Exception e) {
            Notification.show("Error al cargar voluntarios: " + e.getMessage());
        }
    }

    private Div createVolunteerCard(JsonObject volunteer) {
        Div card = new Div();
        card.addClassName("volunteer-card");

        Avatar avatar = new Avatar(volunteer.getString("fullName"));
        avatar.addClassName("volunteer-avatar");

        H3 name = new H3(volunteer.getString("fullName"));
        Span location = new Span("📍 " + volunteer.getString("location"));
        Span skills = new Span("🛠️ " + volunteer.getString("skills"));

        boolean available = volunteer.getBoolean("available");
        Icon statusIcon = available ? VaadinIcon.CHECK_CIRCLE.create() : VaadinIcon.CLOSE_CIRCLE.create();
        statusIcon.setClassName(available ? "status-icon available" : "status-icon unavailable");
        Span statusText = new Span(available ? "Disponible" : "No disponible");
        Div statusLayout = new Div(statusIcon, statusText);
        statusLayout.addClassName("volunteer-status");

        card.add(avatar, name, location, skills, statusLayout);
        card.addClickListener(e -> showVolunteerAlerts((long) volunteer.getNumber("id")));
        card.getStyle().set("cursor", "pointer");

        return card;
    }

    private void showVolunteerProfileForm() {
        contentLayout.removeAll();

        TextField fullName = new TextField("Nombre completo");
        TextField phone = new TextField("Teléfono");
        TextField location = new TextField("Ubicación");
        TextArea skills = new TextArea("Habilidades (ej: Primeros auxilios, rescate...)");

        Button submit = new Button("Crear Perfil", event -> {
            try {
                URL url = new URL("http://localhost:8081/api/volunteers/register");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                SecurityUtils.addAuthHeader(conn);

                String json = String.format("""
                    {
                        "fullName": "%s",
                        "phoneNumber": "%s",
                        "location": "%s",
                        "skills": "%s"
                    }
                """, fullName.getValue(), phone.getValue(), location.getValue(), skills.getValue());

                conn.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));

                if (conn.getResponseCode() == 200) {
                    Notification.show("Perfil creado correctamente.");
                    showVolunteersListView();
                } else {
                    Notification.show("Error al crear perfil: " + conn.getResponseCode());
                }

            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });

        contentLayout.add(fullName, phone, location, skills, submit);
    }

    private void showVolunteerAlerts(Long volunteerId) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeaderTitle("Alertas asignadas");

        VerticalLayout alertsLayout = new VerticalLayout();
        alertsLayout.setSpacing(false);
        alertsLayout.setPadding(false);

        try {
            URL url = new URL("http://localhost:8081/api/volunteer-alerts/" + volunteerId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            SecurityUtils.addAuthHeader(conn);

            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonArray array = (JsonArray) JsonUtil.parse(response);
            if (array.length() == 0) {
                alertsLayout.add(new Paragraph("Este voluntario no está asignado a ninguna alerta."));
            } else {
                for (int i = 0; i < array.length(); i++) {
                    JsonObject alert = array.getObject(i);


                    String title = alert.hasKey("title") && alert.get("title") != null
                            ? alert.getString("title") : "(sin título)";
                    String location = alert.hasKey("location") && alert.get("location") != null
                            ? alert.getString("location") : "(ubicación desconocida)";

                    alertsLayout.add(new Paragraph("🚨 " + title + " — " + location));
                }
            }

        } catch (Exception e) {
            alertsLayout.add(new Paragraph("Error al cargar alertas: " + e.getMessage()));
        }

        dialog.add(alertsLayout);
        dialog.getFooter().add(new Button("Cerrar", e -> dialog.close()));
        dialog.open();
    }


}

