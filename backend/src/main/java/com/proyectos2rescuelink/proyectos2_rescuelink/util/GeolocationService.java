package com.proyectos2rescuelink.proyectos2_rescuelink.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GeolocationService {

    public static double[] getCoordinates(String location) {
        try {
            String encodedLocation = URLEncoder.encode(location, "UTF-8");
            String urlStr = "https://nominatim.openstreetmap.org/search?q=" + encodedLocation + "&format=json&limit=1";

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "RescueLink");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }

            JSONArray arr = new JSONArray(json.toString());
            if (arr.length() > 0) {
                JSONObject obj = arr.getJSONObject(0);
                double lat = obj.getDouble("lat");
                double lon = obj.getDouble("lon");
                return new double[]{lat, lon};
            } else {
                return new double[]{0.0, 0.0};
            }

        } catch (Exception e) {
            System.out.println("❌ Error geolocalizando: " + e.getMessage());
            return new double[]{0.0, 0.0};
        }
    }
}
