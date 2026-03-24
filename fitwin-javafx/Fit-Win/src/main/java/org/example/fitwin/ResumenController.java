package org.example.fitwin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;



public class ResumenController {

    @FXML
    private Label CaloriasEtiq;

    @FXML
    private Label ProteinasEtiq;

    @FXML
    private Label CarbohidratosEtiq;

    @FXML
    private Label GrasasEtiq;

    @FXML
    private Slider BarraAgua;

    @FXML
    private Label AguaEtiq;

    @FXML
    public void initialize() {
        cargarResumenDiario();

        BarraAgua.valueProperty().addListener((obs, oldVal, newVal) -> {
            double litros = Math.round(newVal.doubleValue() * 4) / 4.0;
            int vasos = (int) (litros / 0.25);
            AguaEtiq.setText(String.format("%.2fL agua = %d vasos", litros, vasos));
        });

        BarraAgua.setValue(0);
    }

    private void cargarResumenDiario() {
        Integer usuarioid = SessionControl.getInstance().getUsuarioId();
        if (usuarioid == null) {
            System.out.println("Error: Usuario no autenticado.");
            return;
        }

        try {
            String jsonResponse = ApiClient.getComidasHoy(usuarioid);
            System.out.println("Respuesta de la API: " + jsonResponse);

            JSONArray comidas = new JSONArray(jsonResponse);
            if (comidas.isEmpty()) {
                System.out.println("No hay comidas registradas hoy.");
                return;
            }

            final double[] totalCalorias = {0};
            final double[] totalGrasas = {0};
            final double[] totalCarbohidratos = {0};
            final double[] totalProteinas = {0};

            for (int i = 0; i < comidas.length(); i++) {
                JSONObject comida = comidas.getJSONObject(i);
                double calorias = comida.optDouble("calorias", 0);
                double grasas = comida.optDouble("grasasSaturadas", 0);
                double proteinas = comida.optDouble("proteinas", 0);
                double carbohidratos = comida.optDouble("carbohidratos", 0);

                System.out.println("Comida " + (i + 1) + ": " + comida);
                System.out.println("Calorías: " + calorias);
                System.out.println("Grasas: " + grasas);
                System.out.println("Proteínas: " + proteinas);
                System.out.println("Carbohidratos: " + carbohidratos);

                totalCalorias[0] += calorias;
                totalGrasas[0] += grasas;
                totalProteinas[0] += proteinas;
                totalCarbohidratos[0] += carbohidratos;
            }

            System.out.println("Total Calorías: " + totalCalorias[0]);
            System.out.println("Total Grasas: " + totalGrasas[0]);
            System.out.println("Total Proteínas: " + totalProteinas[0]);
            System.out.println("Total Carbohidratos: " + totalCarbohidratos[0]);

            Platform.runLater(() -> {
                CaloriasEtiq.setText(String.format("%.0f kcal", totalCalorias[0]));
                GrasasEtiq.setText(String.format("%.1f g", totalGrasas[0]));
                ProteinasEtiq.setText(String.format("%.1f g", totalProteinas[0]));
                CarbohidratosEtiq.setText(String.format("%.1f g", totalCarbohidratos[0]));
            });

        } catch (Exception e) {
            System.err.println("Error al cargar el resumen diario: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void onBackToComidaClick(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/org/example/fitwin/comida-view.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la vista: comida-view.fxml");
        }
    }
}
