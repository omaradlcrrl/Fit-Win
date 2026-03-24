package org.example.fitwin;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import java.io.IOException;


public class ComidaController {

    @FXML
    private TextField NombreTxt;

    @FXML
    private TextField CaloriasTxt;

    @FXML
    private TextField GrasasTxt;

    @FXML
    private TextField ProteinasTxt;

    @FXML
    private TextField CarbohidratosTxt;

    @FXML
    private Label EstadoLbl;

    @FXML
    private void onSaveComidaButtonClick(ActionEvent event) {
        if (!validateFields()) {
            showEstado("Por favor, completa todos los campos.", true);
            return;
        }

        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) {
            showEstado("Usuario no autenticado. Inicia sesión.", true);
            return;
        }

        String nombre = NombreTxt.getText();
        String calorias = CaloriasTxt.getText();
        String grasasSaturadas = GrasasTxt.getText();
        String proteinas = ProteinasTxt.getText();
        String carbohidratos = CarbohidratosTxt.getText();

        String jsonBody = """
            {
              "nombre": "%s",
              "calorias": %s,
              "grasasSaturadas": %s,
              "proteinas": %s,
              "carbohidratos": %s,
              "usuarioId": %d
            }
            """.formatted(nombre, calorias, grasasSaturadas, proteinas, carbohidratos, usuarioId);

        try {
            var response = ApiClient.saveComida(jsonBody);
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                showEstado("Comida guardada correctamente.", false);
                clearFields();
            } else {
                showEstado("Error al guardar (" + response.statusCode() + ").", true);
            }
        } catch (Exception e) {
            showEstado("Error de red: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteComidaButtonClick(ActionEvent event) {
        String nombre = NombreTxt.getText();
        if (nombre == null || nombre.isBlank()) {
            showEstado("Indica el nombre de la comida a borrar.", true);
            return;
        }

        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) {
            showEstado("Usuario no autenticado. Inicia sesión.", true);
            return;
        }

        try {
            var response = ApiClient.deleteComidaByNombre(usuarioId, nombre);
            if (response.statusCode() == 200) {
                showEstado("Comida borrada correctamente.", false);
            } else if (response.statusCode() == 404) {
                showEstado("No se encontró esa comida hoy para borrar.", true);
            } else {
                showEstado("Error al borrar (" + response.statusCode() + ").", true);
            }
        } catch (Exception e) {
            showEstado("Error de red: " + e.getMessage(), true);
            e.printStackTrace();
        }
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
            showEstado("No se pudo cargar el menu.", true);
        }
    }

    @FXML
    private void onResumenClick(ActionEvent event) {
        try {
            Parent menuViewParent = FXMLLoader.load(getClass().getResource("/org/example/fitwin/resumen-view.fxml"));
            Scene menuViewScene = new Scene(menuViewParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(menuViewScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            showEstado("No se pudo cargar el resumen ", true);
        }
    }

    private boolean validateFields() {
        return !(NombreTxt.getText().isEmpty()
                || CaloriasTxt.getText().isEmpty()
                || GrasasTxt.getText().isEmpty()
                || ProteinasTxt.getText().isEmpty()
                || CarbohidratosTxt.getText().isEmpty());
    }

    private void clearFields() {
        NombreTxt.clear();
        CaloriasTxt.clear();
        GrasasTxt.clear();
        ProteinasTxt.clear();
        CarbohidratosTxt.clear();
    }

    private void showEstado(String msg, boolean error) {
        EstadoLbl.setText(msg);
        EstadoLbl.setVisible(true);
        EstadoLbl.setManaged(true);

        PauseTransition t = new PauseTransition(Duration.seconds(5));
        t.setOnFinished(ev -> {
            EstadoLbl.setVisible(false);
            EstadoLbl.setManaged(false);
        });
        t.play();
    }
}
