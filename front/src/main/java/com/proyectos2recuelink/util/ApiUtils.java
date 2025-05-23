package com.proyectos2recuelink.util;

import com.proyectos2recuelink.security.SecurityUtils;

import java.net.HttpURLConnection;

public class ApiUtils {

    /**
     * Añade el token JWT al header Authorization de la petición.
     *
     * @param conn conexión HTTP abierta
     */
    public static void addAuthHeader(HttpURLConnection conn) {
        String token = SecurityUtils.getToken();
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
    }
}
