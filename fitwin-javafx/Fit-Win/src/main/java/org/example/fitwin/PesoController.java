package org.example.fitwin;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class PesoController {

    @FXML
    private TextField PesoTxt;

    @FXML
    private Button GuardarBtn;

    @FXML
    private Label EstadoLbl;

    @FXML
    private void initialize() {
        UnaryOperator<TextFormatter.Change> filtroMaxTresDigitos = change -> {
            String nuevo = change.getControlNewText();
            return nuevo.matches("\\d{0,3}") ? change : null;
        };
        PesoTxt.setTextFormatter(new TextFormatter<>(filtroMaxTresDigitos));

        PesoTxt.focusedProperty().addListener((obs, oldF, newF) -> {
            if (newF) {
                if ("0".equals(PesoTxt.getText())) PesoTxt.clear();
            } else {
                String t = PesoTxt.getText();
                if (t == null || t.isBlank()) PesoTxt.setText("0");
            }
        });

        if (GuardarBtn != null) GuardarBtn.setText("");
    }

    @FXML
    private void onGuardarPesoClick(ActionEvent event) {
        String valor = (PesoTxt != null) ? PesoTxt.getText() : "0";
        if (valor == null || valor.isBlank()) valor = "0";

        try {
            double peso = Double.parseDouble(valor);

            Integer usuarioId = SessionControl.getInstance().getUsuarioId();
            if (usuarioId == null) {
                showError("No hay usuario en sesión.");
                return;
            }

            String jsonBody = String.format(Locale.US,
                    "{\"usuarioId\": %d, \"peso\": %.2f}", usuarioId, peso);

            HttpResponse<String> resp = ApiClient.saveRegistroDiario(jsonBody);
            int code = resp.statusCode();

            if (code == 200 || code == 201) {
                showOk("Peso guardado correctamente.");
            } else {
                // Otros códigos = error
                showError("Error al guardar (" + code + "): " + resp.body());
            }

        } catch (NumberFormatException e) {
            showError("Introduce un número válido (0–999).");
        } catch (Exception e) {
            showError("No se pudo conectar con el servidor.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onVolverClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("No se pudo volver al menú.");
        }
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