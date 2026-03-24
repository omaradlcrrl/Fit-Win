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
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;

public class ObjetivosController {

    @FXML
    private Button VolverBtn;

    @FXML
    private Button GenerarBtn;

    @FXML
    private Button SeguimientoBtn;

    @FXML
    private Label  EstadoLbl;

    @FXML
    private void onBackToMenuClick(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/fitwin/menu-view.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            showError("Error al volver al menú.");
        }
    }

    @FXML
    private void onGenerarObjetivosClick(ActionEvent e) {
        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) { showError("Usuario no autenticado."); return; }
        try {
            var resp = ApiClient.generarObjetivos(usuarioId);
            if (resp.statusCode() == 201 || resp.statusCode() == 200) {
                showOk("Objetivos generados correctamente.");
            } else {
                showError("No se pudieron generar los objetivos. Revisa tus datos.");
            }
        } catch (Exception ex) {
            showError("Error de conexión.");
        }
    }

    @FXML
    private void onSeguimientoClick(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/fitwin/objseguimiento-view.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            showOk("El seguimiento estará disponible pronto.");
        }
    }

    private void showOk(String msg)    { showEstado(msg, false); }
    private void showError(String msg) { showEstado(msg, true);  }

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
