package com.proyectos2recuelink.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route(value = "radar", layout = MainLayout.class)
@CssImport("styles/radar.css")
public class RadarView extends VerticalLayout {

    private static final String API_KEY = "91e2f1658a35276e3a5a7a180f4565f3";

    public RadarView() {
        setSizeFull();
        addClassName("radar-view");

        H2 title = new H2("Radar Meteorológico");
        add(title);

        Div mapContainer = new Div();
        mapContainer.setId("map");
        mapContainer.setWidthFull();
        mapContainer.setHeight("500px");
        add(mapContainer);

        Double lat = (Double) VaadinSession.getCurrent().getAttribute("latitude");
        Double lon = (Double) VaadinSession.getCurrent().getAttribute("longitude");

        if (lat == null || lon == null) {
            lat = 40.4168;
            lon = -3.7038;
        }

        double finalLat = lat;
        double finalLon = lon;

        getElement().executeJs(
                "const map = L.map('map').setView([$0, $1], 6);" +
                        "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);" +

                        "const layers = {" +
                        " temp: L.tileLayer('https://tile.openweathermap.org/map/temp_new/{z}/{x}/{y}.png?appid=" + API_KEY + "')," +
                        " wind: L.tileLayer('https://tile.openweathermap.org/map/wind_new/{z}/{x}/{y}.png?appid=" + API_KEY + "')," +
                        " clouds: L.tileLayer('https://tile.openweathermap.org/map/clouds_new/{z}/{x}/{y}.png?appid=" + API_KEY + "')," +
                        " precip: L.tileLayer('https://tile.openweathermap.org/map/precipitation_new/{z}/{x}/{y}.png?appid=" + API_KEY + "')" +
                        "};" +

                        "layers.temp.addTo(map);" +

                        "L.control.layers(null, {" +
                        " '🌡️ Temperatura': layers.temp," +
                        " '💨 Viento': layers.wind," +
                        " '☁️ Nubes': layers.clouds," +
                        " '🌧️ Precipitaciones': layers.precip" +
                        "}).addTo(map);",
                finalLat, finalLon
        );
    }
}
