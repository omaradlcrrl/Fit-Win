package org.example.fitwin;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import org.json.JSONObject;

public class PerfilController {

    @FXML
    private Label PerfilNombre;

    @FXML
    private Label PerfilAltura;

    @FXML
    private Button ModificarPerfilBtn;

    @FXML
    private Button VolverBtn;

    @FXML
    private Button BorrarCuentaBtn;

    @FXML
    private Label EstadoLbl;

    private boolean confirmDeletePending = false;
    private PauseTransition confirmTimer;

    @FXML
    public void initialize() {
        cargarDatosUsuario();

        confirmTimer = new PauseTransition(Duration.seconds(4));
        confirmTimer.setOnFinished(e -> confirmDeletePending = false);
    }

    private void cargarDatosUsuario() {
        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) return;

        try {
            String body = ApiClient.getUsuarioPorId(usuarioId);
            if (body == null) return;

            JSONObject u = new JSONObject(body);
            String nombre = u.optString("nombre", "");
            String apellidos = u.optString("apellidos", "");
            double altura = u.optDouble("altura", 0);

            PerfilNombre.setText((nombre + " " + apellidos).trim());
            PerfilAltura.setText("Altura: " + (altura > 0 ? String.format("%.2f", altura) : "—") + " m");

        } catch (Exception ex) {
            System.err.println("Error al cargar perfil: " + ex.getMessage());
        }
    }

    @FXML
    private void onBackToMenuClick() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/fitwin/menu-view.fxml"));
            Stage stage = (Stage) VolverBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirModificarPerfil() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/fitwin/modifica-view.fxml"));
            Stage stage = (Stage) ModificarPerfilBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void borrarCuenta() {
        if (!confirmDeletePending) {
            confirmDeletePending = true;
            showOk("¿Seguro? Pulsa otra vez para borrar definitivamente.");
            confirmTimer.playFromStart();
            return;
        }

        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId != null) {
            try {
                ApiClient.deleteUsuario(usuarioId);

                Parent root = FXMLLoader.load(getClass().getResource("/org/example/fitwin/login-view.fxml"));
                Stage stage = (Stage) BorrarCuentaBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

                SessionControl.getInstance().cerrar();
            } catch (Exception e) {
                showError("No se pudo borrar la cuenta.");
            } finally {
                confirmDeletePending = false;
                confirmTimer.stop();
            }
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