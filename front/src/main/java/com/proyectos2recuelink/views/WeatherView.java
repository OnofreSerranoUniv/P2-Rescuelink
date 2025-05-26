package com.proyectos2recuelink.views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Route(value = "weather", layout = MainLayout.class)
@CssImport("styles/weather.css") // Estilo personalizado
public class WeatherView extends VerticalLayout {

    private static final String API_KEY = "91e2f1658a35276e3a5a7a180f4565f3";

    public WeatherView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("weather-view");

        //Double lat = (Double) VaadinSession.getCurrent().getAttribute("latitude");//Ubicacion Actual
        //Double lon = (Double) VaadinSession.getCurrent().getAttribute("longitude");//Ubicacion Actual

        //Double lat = 64.1355; //Reykjavik
        //Double lon = -21.8954; //Reykjavik

        Double lat = 40.416729;//Bangkok
        Double lon = -3.703339;//Bangkok

        //Double lat = 43.3619;
        //Double lon = -5.8494;



        if (lat != null && lon != null) {
            JsonNode data = fetchWeather(lat, lon);

            if (data != null) {
                String ciudad = data.get("name").asText();
                JsonNode main = data.get("main");
                JsonNode weather = data.get("weather").get(0);
                JsonNode wind = data.get("wind");

                double temp = main.get("temp").asDouble() - 273.15;
                String estado = weather.get("main").asText();
                String icon = weather.get("icon").asText();
                int humedad = main.get("humidity").asInt();
                double viento = wind.get("speed").asDouble();

                // Emojis opcionales
                String emoji = switch (estado.toLowerCase()) {
                    case "clear" -> "☀️";
                    case "clouds" -> "☁️";
                    case "rain" -> "🌧️";
                    case "thunderstorm" -> "⛈️";
                    case "snow" -> "❄️";
                    default -> "🌈";
                };

                H2 title = new H2("Clima Actual");
                title.addClassName("weather-title");

                H1 city = new H1("📍 " + ciudad);
                city.addClassName("weather-city");

                Span weatherStatus = new Span(emoji + " " + estado);
                weatherStatus.addClassName("weather-status");

                Span tempSpan = new Span("🌡️ Temperatura: " + String.format("%.1f", temp) + " °C");
                tempSpan.addClassName("weather-info");

                Span humiditySpan = new Span("💧 Humedad: " + humedad + "%");
                humiditySpan.addClassName("weather-info");

                Span windSpan = new Span("🌬️ Viento: " + Math.round(viento * 3.6) + " km/h");
                windSpan.addClassName("weather-info");

                add(title, city, weatherStatus, tempSpan, humiditySpan, windSpan);
            } else {
                add(new Span("No se pudo obtener el clima actual."));
            }
        } else {
            add(new Span("No se detectó ubicación. Por favor, accede primero al dashboard."));
        }
    }

    private JsonNode fetchWeather(double lat, double lon) {
        try {
            String urlStr = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s",
                    lat, lon, API_KEY
            );
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
