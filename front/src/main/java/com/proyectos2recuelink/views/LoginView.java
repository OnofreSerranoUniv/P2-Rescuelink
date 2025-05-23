package com.proyectos2recuelink.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Route(value = "login", layout = EmptyLayout.class)
@CssImport("./styles/shared-styles.css")
public class LoginView extends VerticalLayout {

    private ProgressBar progressBar = new ProgressBar();

    public LoginView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        addClassName("login-view");

        H2 title = new H2("Bienvenido a RescueLink");
        title.addClassName("login-title");

        EmailField emailField = new EmailField("Correo electrónico");
        PasswordField passwordField = new PasswordField("Contraseña");

        Button loginButton = new Button("Iniciar sesión", e -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();

            if (email.isEmpty() || password.isEmpty()) {
                Notification.show("Por favor, complete todos los campos.", 3000, Notification.Position.MIDDLE);
                return;
            }

            progressBar.setVisible(true);

            try {
                String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

                URL url = new URL("http://localhost:8081/api/auth/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.getOutputStream().write(body.getBytes("UTF-8"));

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
                    String response = scanner.useDelimiter("\\A").next();
                    scanner.close();

                    JsonValue parsed = JsonUtil.parse(response);
                    if (parsed instanceof JsonObject) {
                        JsonObject json = (JsonObject) parsed;
                        String token = json.getString("token");

                        // Guardamos token y email en sesión
                        VaadinSession.getCurrent().setAttribute("jwt-token", token);
                        VaadinSession.getCurrent().setAttribute("user-email", email);

                        // Hacemos llamada al perfil para recuperar username real
                        URL profileUrl = new URL("http://localhost:8081/api/auth/profile");
                        HttpURLConnection profileConn = (HttpURLConnection) profileUrl.openConnection();
                        profileConn.setRequestMethod("GET");
                        profileConn.setRequestProperty("Authorization", "Bearer " + token);
                        profileConn.setRequestProperty("Accept", "application/json");

                        if (profileConn.getResponseCode() == 200) {
                            Scanner profileScanner = new Scanner(profileConn.getInputStream(), "UTF-8");
                            String profileResponse = profileScanner.useDelimiter("\\A").next();
                            profileScanner.close();

                            JsonValue profileParsed = JsonUtil.parse(profileResponse);
                            if (profileParsed instanceof JsonObject) {
                                JsonObject profileJson = (JsonObject) profileParsed;
                                String username = profileJson.getString("username");
                                VaadinSession.getCurrent().setAttribute("username", username);
                                System.out.println("✅ Username guardado: " + username);
                            } else {
                                System.out.println("⚠️ El perfil recibido no es JSON válido.");
                            }
                        }

                        progressBar.setVisible(false);
                        UI.getCurrent().navigate("dashboard");

                    } else {
                        Notification.show("Respuesta inesperada del servidor.");
                        progressBar.setVisible(false);
                    }

                } else {
                    Notification.show("Credenciales incorrectas.");
                    progressBar.setVisible(false);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Notification.show("Error al iniciar sesión.");
                progressBar.setVisible(false);
            }
        });

        loginButton.addClassName("login-button");
        loginButton.setWidthFull();
        emailField.setWidthFull();
        passwordField.setWidthFull();

        Button registerRedirectButton = new Button("¿No tienes cuenta? Regístrate");
        registerRedirectButton.addClassName("secondary-button");
        registerRedirectButton.addClickListener(ev -> getUI().ifPresent(ui -> ui.navigate("register")));
        registerRedirectButton.setWidthFull();

        VerticalLayout formContainer = new VerticalLayout(emailField, passwordField, loginButton, registerRedirectButton);
        formContainer.setWidth("350px");
        formContainer.setPadding(true);
        formContainer.setSpacing(false);
        formContainer.addClassName("login-form");

        progressBar.setVisible(false);
        progressBar.setWidth("350px");

        add(title, formContainer, progressBar);
    }
}
