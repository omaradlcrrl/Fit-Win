package org.example.fitwin;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class AjustesController {

    @FXML
    private ComboBox<String> ComboEstrategia;

    @FXML
    private Button VolverBtn;

    @FXML
    private Button GuardarBtn;

    @FXML
    private Label EstadoLbl;

    @FXML
    private Label AjustesEtiq;

    private final Preferences prefs = Preferences.userNodeForPackage(AjustesController.class);

    @FXML
    private void initialize() {
        ComboEstrategia.setItems(FXCollections.observableArrayList(
                "SUPERÁVIT", "DÉFICIT", "MANTENIMIENTO"
        ));

        String estrategiaLocal = prefs.get("estrategia_local", "MANTENIMIENTO");
        if (estrategiaLocal != null) estrategiaLocal = estrategiaLocal.toUpperCase();

        if (!ComboEstrategia.getItems().contains(estrategiaLocal)) {
            estrategiaLocal = "MANTENIMIENTO";
        }
        ComboEstrategia.setValue(estrategiaLocal);

        if (AjustesEtiq != null) {
            AjustesEtiq.setText("Modifica tu estrategia");
        }

        if (EstadoLbl != null) {
            EstadoLbl.setVisible(false);
            EstadoLbl.setManaged(false);
        }
    }

    @FXML
    private void guardarEstrategia() {
        String seleccion = ComboEstrategia.getValue();
        if (seleccion == null || seleccion.isBlank()) {
            showError("Selecciona una estrategia.");
            return;
        }

        String valorBackend = mapToBackend(seleccion);
        Integer usuarioId = SessionControl.getInstance().getUsuarioId();

        if (usuarioId == null) {
            showError("Error: usuario no autenticado.");
            return;
        }

        String jsonBody = "{\"estrategia\":\"" + valorBackend + "\"}";

        try {
            var resp = ApiClient.updateUser(usuarioId, jsonBody);
            if (resp.statusCode() == 200) {
                prefs.put("estrategia_local", seleccion);
                showOk("Estrategia actualizada: " + seleccion);
            } else {
                showError("Error al actualizar (" + resp.statusCode() + "): " + resp.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error de conexión: " + e.getMessage());
        }
    }

    private String mapToBackend(String seleccionUI) {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("SUPERÁVIT", "SUPERAVIT");
        mapa.put("SUPÉRAVIT", "SUPERAVIT");
        mapa.put("SUPERAVIT", "SUPERAVIT");
        mapa.put("DÉFICIT", "DEFICIT");
        mapa.put("DEFICIT", "DEFICIT");
        mapa.put("MANTENIMIENTO", "MANTENIMIENTO");

        String key = (seleccionUI == null) ? "" : seleccionUI.toUpperCase();
        return mapa.getOrDefault(key, "MANTENIMIENTO");
    }

    @FXML
        private void onBackToMenuClick(ActionEvent event) {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("/org/example/fitwin/menu-view.fxml"));
                Scene scene = new Scene(parent);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            } catch (IOException e) {
                e.printStackTrace();
                showEstado("No se pudo cargar el menú.", true);
            }
        }



    private void showOk(String msg) {
        showEstado(msg, false);
    }

    private void showError(String msg) {
        showEstado(msg, true);
    }

    private void showEstado(String msg, boolean error) {
        if (EstadoLbl == null) return;

        EstadoLbl.setText(msg);
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
