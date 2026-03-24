package org.example.fitwin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import org.json.JSONObject;

public class ObjSeguimientoController {

    @FXML
    private Button EstrategiaBtn;

    @FXML
    private Label PesoValorLbl;

    @FXML
    private Label AlturaValorLbl;

    @FXML
    private Label ImcValorLbl;

    @FXML
    private Label CaloriasValorLbl;

    @FXML
    private Label ProteinasValorLbl;

    @FXML
    private Label CarbohidratosValorLbl;

    @FXML
    private Label GrasasValorLbl;

    @FXML
    public void initialize() {
        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) {
            alert(Alert.AlertType.ERROR, "Usuario no autenticado.");
            return;
        }

        cargarObjetivos(usuarioId);
    }

    private void cargarObjetivos(Integer usuarioId) {
        try {
            String json = ApiClient.getObjetivoActual(usuarioId);
            if (json == null || json.isBlank()) {
                alert(Alert.AlertType.ERROR, "No se pudo obtener el objetivo.");
                return;
            }
            JSONObject root = new JSONObject(json);
            double peso = root.optDouble("peso", 0);
            double altura = root.optDouble("altura", 0);

            if (altura <= 0) {

            }

            double imc = (altura > 0 && peso > 0) ? peso / (altura * altura) : 0;
            String estrategia = root.optString("tipo", "N/A").toUpperCase();
            double kcal = root.optDouble("caloriasObjetivo", 0);
            double prot = root.optDouble("proteinasObjetivo", 0);
            double carb = root.optDouble("carbohidratosObjetivo", 0);
            double gras = root.optDouble("grasasObjetivo", 0);
            PesoValorLbl.setText(String.format("%.1f kg", peso));
            AlturaValorLbl.setText(String.format("%.2f m", altura));
            ImcValorLbl.setText(String.format("%.1f", imc));
            EstrategiaBtn.setText(estrategia);
            CaloriasValorLbl.setText(String.format("%.0f kcal", kcal));
            ProteinasValorLbl.setText(String.format("%.0f g", prot));
            CarbohidratosValorLbl.setText(String.format("%.0f g", carb));
            GrasasValorLbl.setText(String.format("%.0f g", gras));
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Error cargando objetivos.");
        }
    }

    @FXML
    private void onBackToMenuClick(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/fitwin/menu-view.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            alert(Alert.AlertType.ERROR, "Error al volver al menú.");
        }
    }

    private void alert(Alert.AlertType type, String msg) {
        System.err.println("[ERROR OBJETIVOS] " + msg);
    }
}
