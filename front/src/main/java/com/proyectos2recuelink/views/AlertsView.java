package com.proyectos2recuelink.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;
import com.proyectos2recuelink.security.SecurityUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Route(value = "alerts", layout = MainLayout.class)
@CssImport("styles/alert.css")
public class AlertsView extends VerticalLayout implements BeforeEnterObserver {

    private static final String DEFAULT_IMAGE_URL = "https://images.genius.com/71188f0b7269154a8d6ee7e0b0e77229.300x300x1.jpg";

    private Div alertsContainer = new Div();

    public AlertsView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        alertsContainer.setClassName("alerts-container");
        add(alertsContainer);

        loadAlerts();
    }

    private void loadAlerts() {
        try {
            URL url = new URL("http://localhost:8081/api/alerts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            // ✅ Añadir token de sesión al header
            String token = SecurityUtils.getToken();
            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonArray alertsArray = (JsonArray) JsonUtil.parse(response);

            alertsContainer.removeAll();

            for (int i = 0; i < alertsArray.length(); i++) {
                JsonObject alert = alertsArray.getObject(i);
                alertsContainer.add(createAlertCard(alert));
            }

        } catch (Exception e) {
            Notification.show("Error al cargar alertas: " + e.getMessage());
        }
    }

    private Div createAlertCard(JsonObject alert) {
        Div card = new Div();
        card.addClassName("alert-card");

        // ✅ Usamos alertType para colorear la tarjeta
        String alertType = alert.getString("alertType").toLowerCase(); // ejemplo: "incendio"
        card.addClassName("type-" + alertType);

        Image image = new Image(DEFAULT_IMAGE_URL, "Imagen genérica");
        image.setWidth("100%");
        image.setHeight("150px");

        H4 title = new H4(alert.getString("title"));
        Paragraph location = new Paragraph("Ubicación: " + alert.getString("location"));
        Paragraph type = new Paragraph("Tipo: " + alert.getString("alertType"));
        Paragraph date = new Paragraph("Fecha: " + alert.getString("formattedDate"));

        Button detailsButton = new Button("Ver Detalles", event -> {
            int alertId = (int) alert.getNumber("id");
            getUI().ifPresent(ui -> ui.navigate("alert-detail/" + alertId));
        });

        card.add(image, title, location, type, date, detailsButton);
        return card;
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isAuthenticated()) {
            Notification.show("Debes iniciar sesión primero.");
            event.forwardTo("login");
        }
    }
}
