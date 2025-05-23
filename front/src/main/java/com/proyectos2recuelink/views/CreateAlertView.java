package com.proyectos2recuelink.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import elemental.json.JsonObject;
import com.proyectos2recuelink.security.SecurityUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.proyectos2recuelink.util.ApiUtils.addAuthHeader;

@PageTitle("Crear Alerta")
@Route(value = "create-alert", layout = MainLayout.class)
@CssImport("styles/createalert.css")
public class CreateAlertView extends VerticalLayout implements BeforeEnterObserver {

    private TextField titleField;
    private TextArea descriptionField;
    private TextField locationField;
    private ComboBox<String> alertTypeField;

    public CreateAlertView() {
        addClassName("create-alert-container");
        setSizeFull();

        H1 title = new H1("Crear Nueva Alerta");
        title.addClassName("create-alert-title");

        titleField = new TextField("Título");
        descriptionField = new TextArea("Descripción");
        locationField = new TextField("Ubicación");

        alertTypeField = new ComboBox<>("Tipo de Alerta");
        alertTypeField.setItems("Incendio", "Inundación", "Terremoto", "Tormenta", "General");
        alertTypeField.setPlaceholder("Selecciona un tipo");
        alertTypeField.setClearButtonVisible(true);

        Button submitButton = new Button("Crear Alerta", event -> createAlert());
        submitButton.addClassName("create-alert-button");

        Div formWrapper = new Div();
        formWrapper.addClassName("create-alert-form");
        formWrapper.add(title, titleField, descriptionField, locationField, alertTypeField, submitButton);

        add(formWrapper);
    }

    private void createAlert() {
        try {
            URL url = new URL("http://localhost:8081/api/alerts/create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // ✅ Añadir el token de seguridad
            addAuthHeader(conn);

            JsonObject json = Json.createObject();
            json.put("title", titleField.getValue());
            json.put("description", descriptionField.getValue());
            json.put("location", locationField.getValue());

            String selectedType = alertTypeField.getValue() != null ? alertTypeField.getValue().toLowerCase() : "general";
            json.put("alertType", selectedType);

            byte[] input = json.toJson().getBytes(StandardCharsets.UTF_8);
            conn.getOutputStream().write(input);

            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            Notification.show("Alerta creada correctamente.");
            getUI().ifPresent(ui -> ui.navigate("alerts"));

        } catch (Exception e) {
            Notification.show("Error al crear alerta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isAuthenticated()) {
            Notification.show("Debes iniciar sesión primero.");
            event.forwardTo("login");
        }
    }
}
