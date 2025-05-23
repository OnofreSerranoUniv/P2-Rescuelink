package com.proyectos2recuelink.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Route(value = "register", layout = EmptyLayout.class)
@CssImport("./styles/shared-styles.css")
public class RegisterView extends VerticalLayout {

    private ProgressBar progressBar = new ProgressBar();

    public RegisterView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        addClassName("login-view");

        H2 title = new H2("Crea tu cuenta en RescueLink");
        title.addClassName("login-title");

        TextField usernameField = new TextField("Usuario");
        EmailField emailField = new EmailField("Correo electrónico");
        PasswordField passwordField = new PasswordField("Contraseña");
        PasswordField confirmPasswordField = new PasswordField("Repite la contraseña");
        Checkbox volunteerCheckbox = new Checkbox("Quiero registrarme como voluntario");
        Checkbox termsCheckbox = new Checkbox("Acepto los términos y condiciones");

        Button registerButton = new Button("Registrarse", event ->
                registerUser(
                        usernameField.getValue(),
                        emailField.getValue(),
                        passwordField.getValue(),
                        confirmPasswordField.getValue(),
                        volunteerCheckbox.getValue(),
                        termsCheckbox.getValue()
                ));

        registerButton.setWidthFull();
        registerButton.addClassName("register-button");

        usernameField.setWidthFull();
        emailField.setWidthFull();
        passwordField.setWidthFull();
        confirmPasswordField.setWidthFull();
        volunteerCheckbox.setWidthFull();
        termsCheckbox.setWidthFull();

        Button loginRedirectButton = new Button("¿Ya tienes cuenta? Inicia sesión");
        loginRedirectButton.addClassName("secondary-button");
        loginRedirectButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("login")));
        loginRedirectButton.setWidthFull();

        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("350px");
        formContainer.setPadding(true);
        formContainer.setSpacing(false);
        formContainer.addClassName("login-form");

        formContainer.add(
                usernameField,
                emailField,
                passwordField,
                confirmPasswordField,
                volunteerCheckbox,
                termsCheckbox,
                registerButton,
                loginRedirectButton
        );

        progressBar.setVisible(false);
        progressBar.setWidth("350px");
        add(title, formContainer, progressBar);
    }


    private void registerUser(String username, String email, String password, String confirmPassword, boolean isVolunteer, boolean termsAccepted) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Notification.show("Todos los campos son obligatorios.", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            Notification.show("Las contraseñas no coinciden.", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (!termsAccepted) {
            Notification.show("Debe aceptar los términos y condiciones.", 3000, Notification.Position.MIDDLE);
            return;
        }

        progressBar.setVisible(true);

        try {
            URL url = new URL("http://localhost:8081/api/auth/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject json = Json.createObject();
            json.put("username", username);
            json.put("email", email);
            json.put("password", password);
            json.put("isVolunteer", isVolunteer);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toJson().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            Scanner scanner = new Scanner(conn.getInputStream(), "utf-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            progressBar.setVisible(false);
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (Exception e) {
            progressBar.setVisible(false);
            Notification.show("Error en el registro: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}
