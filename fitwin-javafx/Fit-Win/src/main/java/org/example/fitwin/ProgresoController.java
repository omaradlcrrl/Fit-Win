package org.example.fitwin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProgresoController {

    @FXML
    private void onBackToMenuClick(ActionEvent event) {
        navigate(event, "/org/example/fitwin/menu-view.fxml", "FITWIN — MENÚ");
    }

    @FXML
    private void onPesoClick(ActionEvent event) {
        navigate(event, "/org/example/fitwin/progresop-view.fxml", "FITWIN — PROGRESO DE PESO");
    }

    @FXML
    private void onMedidasClick(ActionEvent event) {
        navigate(event, "/org/example/fitwin/progresom-view.fxml", "FITWIN — PROGRESO DE MEDICIONES");
    }

    private void navigate(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setTitle(title);
            window.show();
        } catch (Exception e) {
            System.err.println("Error al cargar: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
