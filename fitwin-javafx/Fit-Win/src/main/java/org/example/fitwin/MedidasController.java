package org.example.fitwin;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDate;

public class MedidasController {

    @FXML
    private TextField BrazoTxt;

    @FXML
    private TextField PechoTxt;

    @FXML
    private TextField EspaldaTxt;

    @FXML
    private TextField MusloTxt;

    @FXML
    private TextField HombroTxt;

    @FXML
    private Label EstadoLbl;

    private boolean confirmDeletePending = false;
    private PauseTransition confirmTimer;

    @FXML
    private void initialize() {
        if (confirmTimer == null) {
            confirmTimer = new PauseTransition(Duration.seconds(4));
            confirmTimer.setOnFinished(e -> confirmDeletePending = false);
        }
    }

    @FXML
    private void onGuardarMedidasClick(ActionEvent e) {
        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) {
            showError("Usuario no autenticado");
            return;
        }

        Double brazo   = parseDoubleOrNull(BrazoTxt.getText());
        Double pecho   = parseDoubleOrNull(PechoTxt.getText());
        Double espalda = parseDoubleOrNull(EspaldaTxt.getText());
        Double muslo   = parseDoubleOrNull(MusloTxt.getText());
        Double hombro  = parseDoubleOrNull(HombroTxt.getText());

        if (brazo == null && pecho == null && espalda == null && muslo == null && hombro == null) {
            showOk("Introduce al menos una medida para guardar.");
            return;
        }

        String brazoJson   = (brazo == null)   ? "null" : brazo.toString();
        String pechoJson   = (pecho == null)   ? "null" : pecho.toString();
        String espaldaJson = (espalda == null) ? "null" : espalda.toString();
        String musloJson   = (muslo == null)   ? "null" : muslo.toString();
        String hombroJson  = (hombro == null)  ? "null" : hombro.toString();

        String jsonBody = """
            {
              "usuarioId": %d,
              "brazo": %s,
              "pecho": %s,
              "espalda": %s,
              "muslo": %s,
              "hombro": %s
            }
            """.formatted(usuarioId, brazoJson, pechoJson, espaldaJson, musloJson, hombroJson);

        try {
            var resp = ApiClient.saveMedicion(jsonBody);
            int code = resp.statusCode();

            if (code == 201) {
                showOk("Medición de hoy guardada correctamente.");
                return;
            }

            if (code == 409) {
                int medicionIdHoy = extraerMedicionIdDeHoy(usuarioId);
                if (medicionIdHoy == -1) {
                    showError("No se pudo localizar la medición de hoy para actualizar.");
                    return;
                }

                var respUpdate = ApiClient.updateMedicion(medicionIdHoy, jsonBody);
                int codeUpdate = respUpdate.statusCode();

                if (codeUpdate == 200) {
                    showOk("Medición de hoy actualizada correctamente.");
                } else {
                    showError("Error al actualizar (" + codeUpdate + "): " + respUpdate.body());
                }
                return;
            }

            showError("Error al guardar (" + code + "): " + resp.body());

        } catch (Exception ex) {
            showError("Error al comunicarse con la API: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private int extraerMedicionIdDeHoy(int usuarioId) {
        try {
            LocalDate hoy = LocalDate.now();
            String json = ApiClient.getMedicionesRango(usuarioId, hoy, hoy);
            if (json == null || json.isBlank() || json.equals("[]")) {
                return -1;
            }

            JSONArray arr = new JSONArray(json);
            if (arr.isEmpty()) {
                return -1;
            }

            JSONObject obj = arr.getJSONObject(0);
            return obj.getInt("medicionId");

        } catch (Exception ex) {
            System.err.println("Error al obtener medicionId de hoy: " + ex.getMessage());
            return -1;
        }
    }


    @FXML
    private void onBorrarMedidasClick(ActionEvent e) {
        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) {
            showError("Usuario no autenticado");
            return;
        }

        if (!confirmDeletePending) {
            confirmDeletePending = true;
            showOk("Pulsa de nuevo para eliminar la medición de hoy.");
            confirmTimer.playFromStart();
            return;
        }

        try {
            var resp = ApiClient.deleteMedicionHoy(usuarioId);
            int code = resp.statusCode();
            if (code == 200) {
                showOk("Medición de hoy eliminada correctamente.");
                BrazoTxt.clear();
                PechoTxt.clear();
                EspaldaTxt.clear();
                MusloTxt.clear();
                HombroTxt.clear();
            } else if (code == 404) {
                showOk("No existe medición de hoy para eliminar.");
            } else {
                showError("Error al eliminar (" + code + "): " + resp.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error al comunicarse con la API: " + ex.getMessage());
        } finally {
            confirmDeletePending = false;
            confirmTimer.stop();
        }
    }

    @FXML
    private void onBackToMenuClick(ActionEvent event) {
        try {
            Parent menuViewParent = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
            Scene menuViewScene = new Scene(menuViewParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(menuViewScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("No se pudo cargar el menú principal.");
        }
    }


    private static Double parseDoubleOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s.trim().replace(",", ".")); }
        catch (NumberFormatException ex) { return null; }
    }

    private void showOk(String msg) { showEstado(msg, false); }
    private void showError(String msg) { showEstado(msg, true); }

    private void showEstado(String msg, boolean error) {
        if (EstadoLbl == null) return;
        EstadoLbl.setText(msg);
        EstadoLbl.getStyleClass().removeAll("ok", "error");
        EstadoLbl.getStyleClass().add(error ? "error" : "ok");
        if (!EstadoLbl.getStyleClass().contains("alerta-pill")) {
            EstadoLbl.getStyleClass().add("alerta-pill");
        }
        EstadoLbl.setVisible(true);
        EstadoLbl.setManaged(true);
        PauseTransition t = new PauseTransition(Duration.seconds(3));
        t.setOnFinished(ev -> {
            EstadoLbl.setVisible(false);
            EstadoLbl.setManaged(false);
        });
        t.play();
    }
}
