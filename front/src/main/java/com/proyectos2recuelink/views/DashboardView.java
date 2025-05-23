package com.proyectos2recuelink.views;

import com.proyectos2recuelink.security.SecurityUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Route(value = "dashboard", layout = MainLayout.class)
@CssImport("styles/dashboard.css")
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        setSizeFull();
        addClassName("dashboard-view");

        // 1. Ejecutar JS para obtener ubicación del usuario
        UI.getCurrent().getPage().executeJs(
                "navigator.geolocation.getCurrentPosition(function(pos) {" +
                        "   const lat = pos.coords.latitude;" +
                        "   const lon = pos.coords.longitude;" +
                        "   $0.$server.saveLocation(lat, lon);" +
                        "});",
                getElement()
        );



        // 2. Mostrar correctamente el nombre de usuario
        String username = SecurityUtils.getUsername().orElse("Usuario");
        H1 title = new H1("Hola, " + username);
        title.addClassName("dashboard-title");

        // 3. Layout principal
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setPadding(false);
        mainLayout.addClassName("main-dashboard-layout");

        // 4. Cargar alertas activas
        List<String[]> activeAlerts = fetchActiveAlerts();

        // 5. Obtener lat/lon desde sesión
        Double lat = null, lon = null;
        if (VaadinSession.getCurrent() != null) {
            lat = (Double) VaadinSession.getCurrent().getAttribute("latitude");
            lon = (Double) VaadinSession.getCurrent().getAttribute("longitude");
        }

        double finalLat = lat != null ? lat : 40.4168;
        double finalLon = lon != null ? lon : -3.7038;

        // 6. Obtener ciudad a partir de lat/lon
        String ciudad = "Ubicación desconocida";
        try {
            String apiUrl = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s", finalLat, finalLon, "91e2f1658a35276e3a5a7a180f4565f3");
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response);
                ciudad = root.get("name").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 7. Sección Ubicación + Mapa
        HorizontalLayout locationAndMapLayout = new HorizontalLayout();
        locationAndMapLayout.setWidthFull();
        locationAndMapLayout.setSpacing(true);
        locationAndMapLayout.addClassName("location-map-section");

        // Sección ubicación
        Div ubicacionAlertasSection = createLargeSection(
                "Ubicación Actual",
                ciudad + ", " + activeAlerts.size() + " alertas activas"
        );
        ubicacionAlertasSection.addClassName("ubicacion-section");

        // 🗺️ Mapa
        Div mapContainer = new Div();
        mapContainer.setId("map-dashboard");
        mapContainer.addClassName("map-dashboard");
        mapContainer.setWidth("100%");
        mapContainer.setHeight("300px");

        // Añadimos ambos al layout horizontal
        locationAndMapLayout.add(ubicacionAlertasSection, mapContainer);
        locationAndMapLayout.setFlexGrow(1, ubicacionAlertasSection, mapContainer);

        // Añadir al layout principal
        mainLayout.add(locationAndMapLayout);

        // 8. Insertar mapa interactivo
        UI.getCurrent().getPage().executeJs(
                "const map = L.map('map-dashboard').setView([$0, $1], 6);" +
                        "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);" +
                        "const layers = {" +
                        " temp: L.tileLayer('https://tile.openweathermap.org/map/temp_new/{z}/{x}/{y}.png?appid=91e2f1658a35276e3a5a7a180f4565f3')," +
                        " wind: L.tileLayer('https://tile.openweathermap.org/map/wind_new/{z}/{x}/{y}.png?appid=91e2f1658a35276e3a5a7a180f4565f3')," +
                        " clouds: L.tileLayer('https://tile.openweathermap.org/map/clouds_new/{z}/{x}/{y}.png?appid=91e2f1658a35276e3a5a7a180f4565f3')," +
                        " precip: L.tileLayer('https://tile.openweathermap.org/map/precipitation_new/{z}/{x}/{y}.png?appid=91e2f1658a35276e3a5a7a180f4565f3')" +
                        "};" +
                        "layers.temp.addTo(map);" +
                        "L.control.layers(null, {" +
                        " 'Temperatura': layers.temp," +
                        " 'Viento': layers.wind," +
                        " 'Nubes': layers.clouds," +
                        " 'Precipitaciones': layers.precip" +
                        "}).addTo(map);",
                finalLat, finalLon
        );

        // 9. Sección alertas + voluntariados
        HorizontalLayout horizontalCards = new HorizontalLayout();
        horizontalCards.setWidthFull();
        horizontalCards.setSpacing(true);

        Div alertasCard = createAlertasCercanasCard("Alertas Cercanas", VaadinIcon.BELL, activeAlerts);
        alertasCard.getStyle().set("cursor", "pointer");
        alertasCard.addClickListener(e -> UI.getCurrent().navigate(AlertsView.class));

        Div voluntariadoCard = createWideCard("Mis Voluntariados", VaadinIcon.USER_HEART);
        voluntariadoCard.getStyle().set("cursor", "pointer");
        voluntariadoCard.addClickListener(e -> UI.getCurrent().navigate(VolunteersView.class));

        horizontalCards.add(alertasCard, voluntariadoCard);

        // 10. Sección acceso rápido
        HorizontalLayout quickAccessCards = new HorizontalLayout();
        quickAccessCards.setWidthFull();
        quickAccessCards.setSpacing(true);

        quickAccessCards.add(
                createQuickAccessCard("Radar de Emergencias", VaadinIcon.CHART),
                createQuickAccessCard("Clima Actual", VaadinIcon.CLOUD),
                createQuickAccessCard("Guías de Emergencia", VaadinIcon.BOOK)
        );

        mainLayout.add(horizontalCards, quickAccessCards);
        add(title, mainLayout);
    }



    private List<String[]> fetchActiveAlerts() {
        List<String[]> alertas = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8081/api/alerts/active");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // ✅ Añadir token
            String token = SecurityUtils.getToken();
            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }


            Scanner scanner = new Scanner(conn.getInputStream(), "utf-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonArray jsonArray = JsonUtil.parse(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JsonObject obj = jsonArray.getObject(i);
                String titulo = obj.getString("title");
                String lugar = obj.getString("location");
                alertas.add(new String[]{titulo, lugar});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alertas;
    }

    private List<String[]> fetchVolunteerAlerts() {
        List<String[]> voluntariados = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8081/api/volunteers/me/alerts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            SecurityUtils.addAuthHeader(conn);

            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonArray jsonArray = JsonUtil.parse(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JsonObject alert = jsonArray.getObject(i);
                String title = alert.getString("title");
                String location = alert.getString("location");
                voluntariados.add(new String[]{title, location});
            }

        } catch (Exception e) {
            System.out.println("❌ Error obteniendo voluntariados: " + e.getMessage());
        }

        return voluntariados;
    }


    private Div createAlertasCercanasCard(String title, VaadinIcon icon, List<String[]> alertas) {
        Div card = new Div();
        card.addClassName("dashboard-wide-card");

        Icon vaadinIcon = icon.create();
        vaadinIcon.addClassName("wide-card-icon");

        Span titleLabel = new Span(title);
        titleLabel.addClassName("wide-card-title");

        VerticalLayout alertList = new VerticalLayout();
        alertList.setPadding(false);
        alertList.setSpacing(false);
        alertList.addClassName("alert-list");

        for (String[] alerta : alertas) {
            Span alertaSpan = new Span(alerta[0] + " — " + alerta[1]);
            alertaSpan.addClassName("alert-item");
            alertList.add(alertaSpan);
        }

        card.add(vaadinIcon, titleLabel, alertList);
        return card;
    }

    private Div createLargeSection(String title, String description) {
        Div section = new Div();
        section.addClassName("dashboard-large-section");

        Span titleLabel = new Span(title);
        titleLabel.addClassName("section-title");

        Span descLabel = new Span(description);
        descLabel.addClassName("section-desc");

        section.add(titleLabel, descLabel);
        return section;
    }

    private Div createWideCard(String title, VaadinIcon icon) {
        Div card = new Div();
        card.addClassName("dashboard-wide-card");

        Icon vaadinIcon = icon.create();
        vaadinIcon.addClassName("wide-card-icon");

        Span titleLabel = new Span(title);
        titleLabel.addClassName("wide-card-title");

        Button button = new Button("Ver más");
        button.addClassName("wide-card-button");

        VerticalLayout alertList = new VerticalLayout();
        alertList.setPadding(false);
        alertList.setSpacing(false);
        alertList.addClassName("alert-list");

        if (title.equals("Mis Voluntariados")) {
            List<String[]> voluntariados = fetchVolunteerAlerts();
            for (String[] v : voluntariados) {
                Span alertaSpan = new Span("🚨 " + v[0] + " — " + v[1]);
                alertaSpan.addClassName("alert-item");
                alertList.add(alertaSpan);
            }

            button.addClickListener(e -> UI.getCurrent().navigate(VolunteersView.class));
        }

        card.add(vaadinIcon, titleLabel, alertList, button);
        return card;
    }


    private Div createQuickAccessCard(String title, VaadinIcon icon) {
        Div card = new Div();
        card.addClassName("dashboard-quick-card");

        Icon vaadinIcon = icon.create();
        vaadinIcon.addClassName("quick-card-icon");

        Span titleLabel = new Span(title);
        titleLabel.addClassName("quick-card-title");

        // Navegación según título
        card.addClickListener(e -> {
            switch (title) {
                case "Radar de Emergencias" -> UI.getCurrent().navigate("radar");
                case "Clima Actual" -> UI.getCurrent().navigate("weather");
                case "Guías de Emergencia" -> UI.getCurrent().navigate("guides");
            }
        });

        card.getStyle().set("cursor", "pointer");
        card.add(vaadinIcon, titleLabel);
        return card;
    }

    @com.vaadin.flow.component.ClientCallable
    public void saveLocation(double latitude, double longitude) {
        com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("latitude", latitude);
        com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("longitude", longitude);
        System.out.println("📍 Ubicación guardada en sesión: " + latitude + ", " + longitude);
    }



}
