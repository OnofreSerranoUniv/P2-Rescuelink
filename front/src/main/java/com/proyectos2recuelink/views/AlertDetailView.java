package com.proyectos2recuelink.views;

import com.proyectos2recuelink.security.SecurityUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@PageTitle("Detalles de la Alerta")
@Route(value = "alert-detail/:alertId", layout = MainLayout.class)
@CssImport("styles/alertdetail.css")
public class AlertDetailView extends VerticalLayout implements BeforeEnterObserver {

    private static final String DEFAULT_IMAGE_URL = "https://img.freepik.com/vector-premium/senal-alerta-roja-sobre-fondo-rojo-advertencia-sombra-atencion_568973-490.jpg";
    private Long alertId;

    public AlertDetailView() {
        addClassName("alert-detail-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isAuthenticated()) {
            Notification.show("Debes iniciar sesión primero.");
            event.forwardTo("login");
            return;
        }

        event.getRouteParameters().get("alertId").ifPresent(id -> {
            try {
                this.alertId = Long.parseLong(id);
                loadAlertDetails();
            } catch (NumberFormatException e) {
                Notification.show("Error: ID de alerta no válido");
                getUI().ifPresent(ui -> ui.navigate("alerts"));
            }
        });
    }

    private void loadAlertDetails() {
        try {
            URL url = new URL("http://localhost:8081/api/alerts/" + alertId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            // ✅ Añadir token
            String token = SecurityUtils.getToken();
            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonObject alert = (JsonObject) JsonUtil.parse(response);

            // Construir UI
            Div card = new Div();
            card.addClassName("alert-detail-card");

            String imageUrl = alert.hasKey("imageUrl") ? alert.getString("imageUrl") : DEFAULT_IMAGE_URL;
            Image image = new Image(imageUrl, "Imagen de la alerta");
            image.addClassName("alert-detail-image");

            H2 title = new H2(alert.getString("title"));
            title.addClassName("alert-detail-title");

            Paragraph type = new Paragraph("Tipo: " + capitalize(alert.getString("alertType")));
            Paragraph location = new Paragraph("Ubicación: " + alert.getString("location"));
            Paragraph date = new Paragraph("Fecha: " + alert.getString("timestamp"));
            Paragraph description = new Paragraph(alert.getString("description"));

            type.addClassName("alert-detail-meta");
            location.addClassName("alert-detail-meta");
            date.addClassName("alert-detail-meta");
            description.addClassName("alert-detail-description");

            Button backButton = new Button("← Volver a Alertas", event -> getUI().ifPresent(ui -> ui.navigate("alerts")));
            backButton.addClassName("alert-detail-button");

            card.add(image, title, type, location, date, description, backButton);

            // ✅ Añadir botón para unirse si es voluntario y tiene perfil
            checkAndAddJoinButton(card);

            add(card);

        } catch (Exception e) {
            Notification.show("Error al cargar detalles: " + e.getMessage());
        }
    }

    private void checkAndAddJoinButton(Div card) {
        try {
            URL checkUrl = new URL("http://localhost:8081/api/volunteers/me");
            HttpURLConnection checkConn = (HttpURLConnection) checkUrl.openConnection();
            checkConn.setRequestMethod("GET");
            SecurityUtils.addAuthHeader(checkConn);

            if (checkConn.getResponseCode() == 200) {
                Scanner checkScanner = new Scanner(checkConn.getInputStream(), "UTF-8");
                String checkResponse = checkScanner.useDelimiter("\\A").next();
                checkScanner.close();

                JsonObject checkJson = (JsonObject) JsonUtil.parse(checkResponse);
                boolean isVolunteer = checkJson.getBoolean("isVolunteer");
                boolean hasProfile = checkJson.getBoolean("hasProfile");

                if (isVolunteer && hasProfile) {
                    Button joinButton = new Button("🆘 Unirme a esta alerta", ev -> joinAlert(alertId));
                    joinButton.getStyle().set("margin-top", "20px");
                    card.add(joinButton);
                }
            }
        } catch (Exception e) {
            Notification.show("Error al comprobar si puedes unirte: " + e.getMessage());
        }
    }

    private void joinAlert(Long alertId) {
        try {
            URL url = new URL("http://localhost:8081/api/alerts/" + alertId + "/join");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            SecurityUtils.addAuthHeader(conn);

            if (conn.getResponseCode() == 200) {
                Notification.show("Te has unido a la alerta exitosamente.");
            } else if (conn.getResponseCode() == 409) {
                Notification.show("Ya estás unido a esta alerta.");
            } else {
                Notification.show("Error al unirse: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            Notification.show("❌ Error al enviar solicitud: " + e.getMessage());
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
