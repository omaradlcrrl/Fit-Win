package org.example.fitwin.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:3036/api/v1/FWBBD";

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /* utils */

    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    private static void debug(String tag, HttpResponse<String> response) {
        System.out.println("[API] " + tag + " -> " + response.statusCode());
        String body = response.body();
        if (body != null && !body.isBlank()) {
            System.out.println("[API] body: " + body);
        }
    }

    /* usuarios */

    public static HttpResponse<String> registerUser(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/usuarios/save"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("POST /usuarios/save", response);
        return response;
    }

    public static HttpResponse<String> loginUser(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/usuarios/login"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("POST /usuarios/login", response);
        return response;
    }


    public static String getUsuarioPorId(int usuarioId) {
        String url = BASE_URL + "/usuarios/" + usuarioId;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            debug("GET /usuarios/{id}", response);

            return response.statusCode() == 200 ? response.body() : null;

        } catch (Exception e) {
            System.err.println("Error en la peticion: " + e.getMessage());
            return null;
        }
    }

    public static HttpResponse<String> updateUser(int usuarioId, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/usuarios/actualizar/" + usuarioId))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("PUT /usuarios/actualizar/{id}", response);
        return response;
    }


    public static java.net.http.HttpResponse<String> deleteUsuario(int usuarioId) throws Exception {
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/usuarios/" + usuarioId))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        debug("DELETE /usuarios/{id}", response);
        return response;
    }
    /* comidas */

    public static HttpResponse<String> saveComida(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/comidas/save"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("POST /comidas/save", response);
        return response;
    }

    public static HttpResponse<String> deleteComidaByNombre(int usuarioId, String nombre) throws Exception {
        String url = BASE_URL + "/comidas/deleteByNombre?usuarioId=" + usuarioId + "&nombre=" + encode(nombre);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("DELETE /comidas/deleteByNombre", response);
        return response;
    }

    public static String getComidasHoy(int usuarioId) {
        String url = BASE_URL + "/comidas/hoy/" + usuarioId;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            debug("GET /comidas/hoy/{usuarioId}", response);
            return response.statusCode() == 200 ? response.body() : "[]";

        } catch (Exception e) {
            return "[]";
        }
    }

    /* ejercicios */

    @Deprecated
    public static HttpResponse<String> deleteEjercicioByPosicion(int usuarioId, int posicion) throws Exception {
        String url = BASE_URL + "/entrenamientos/deleteByPosicion?usuarioId=" + usuarioId + "&posicion=" + posicion;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("DELETE /entrenamientos/deleteByPosicion", response);
        return response;
    }

    public static HttpResponse<String> deleteEjercicioByPosicionYDia(int usuarioId, String diaSemana, int posicion) throws Exception {
        String url = BASE_URL + "/entrenamientos/deleteByPosicionYDia"
                + "?usuarioId=" + usuarioId
                + "&diaSemana=" + encode(diaSemana)
                + "&posicion=" + posicion;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("DELETE /entrenamientos/deleteByPosicionYDia", response);
        return response;
    }


    public static HttpResponse<String> saveEjercicio(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/entrenamientos/save"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("POST /entrenamientos/save", response);
        return response;
    }



    public static String getEjerciciosPorDia(int usuarioId, String diaSemana) {
        String url = BASE_URL + "/entrenamientos/findByUserAndDay"
                + "?usuarioId=" + usuarioId
                + "&diaSemana=" + encode(diaSemana);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            debug("GET /entrenamientos/findByUserAndDay", response);
            return response.statusCode() == 200 ? response.body() : "[]";

        } catch (Exception e) {
            return "[]";
        }
    }

    /* registros diarios */

    public static HttpResponse<String> saveRegistroDiario(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/registrosdiarios/save"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("POST /registrosdiarios/save", response);
        return response;
    }

    public static String getRegistrosDiariosRango(int usuarioId, LocalDate from, LocalDate to) {
        String url = BASE_URL + "/registrosdiarios/range/" + usuarioId + "?from=" + from + "&to=" + to;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            debug("GET /registrosdiarios/range/{usuarioId}", response);
            return response.statusCode() == 200 ? response.body() : "[]";

        } catch (Exception e) {
            return "[]";
        }
    }


    /* mediciones */


    public static HttpResponse<String> saveMedicion(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mediciones/save"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("POST /mediciones/save", response);
        return response;
    }

    public static HttpResponse<String> updateMedicion(int medicionId, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mediciones/actualizar/" + medicionId))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("PUT /mediciones/actualizar/{id}", response);
        return response;
    }


    public static HttpResponse<String> deleteMedicionHoy(int usuarioId) throws Exception {
        String url = BASE_URL + "/mediciones/deleteHoy/" + usuarioId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("DELETE /mediciones/deleteHoy", response);
        return response;
    }

    public static String getMedicionesRango(int usuarioId, LocalDate from, LocalDate to) {
        String url = BASE_URL + "/mediciones/range/" + usuarioId + "?from=" + from + "&to=" + to;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            debug("GET /mediciones/range/{usuarioId}", response);
            return response.statusCode() == 200 ? response.body() : "[]";

        } catch (Exception e) {
            return "[]";
        }
    }

    /* objetivos */

    public static HttpResponse<String> generarObjetivos(int usuarioId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/objetivos/generar/" + usuarioId))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        debug("POST /objetivos/generar/{usuarioId}", response);
        return response;
    }

    public static String getObjetivoActual(int usuarioId) {
        String url = BASE_URL + "/objetivos/actual/" + usuarioId;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            debug("GET /objetivos/actual/{usuarioId}", response);
            return response.statusCode() == 200 ? response.body() : null;

        } catch (Exception e) {
            return null;
        }
    }
}
