package org.example.fitwin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;




public class ProgresoPController {

    @FXML
    private Button VolverBtn;

    @FXML
    private Button CalcularBtn;

    @FXML
    private DatePicker desdePicker;

    @FXML
    private DatePicker hastaPicker;

    @FXML
    private Label ResultadoLbl;

    @FXML
    private LineChart<String, Number> pesoChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private Integer usuarioId;

    @FXML
    private void initialize() {
        usuarioId = Optional.ofNullable(SessionControl.getInstance().getUsuarioId()).orElse(-1);
        desdePicker.setValue(LocalDate.now().minusDays(30));
        hastaPicker.setValue(LocalDate.now());
        ResultadoLbl.setText("");
        pesoChart.getData().clear();
        onCalcularClick();
    }

    @FXML
    private void onCalcularClick() {
        LocalDate desde = desdePicker.getValue();
        LocalDate hasta = hastaPicker.getValue();

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            ResultadoLbl.setText("Selecciona un rango válido.");
            pesoChart.getData().clear();
            return;
        }

        String json = ApiClient.getRegistrosDiariosRango(usuarioId, desde, hasta);
        Map<LocalDate, Double> pesos = parsePesos(json);

        if (pesos.isEmpty()) {
            ResultadoLbl.setText("No hay registros en el rango seleccionado.");
            pesoChart.getData().clear();
            return;
        }

        dibujarGrafica(pesos);

        LocalDate min = pesos.keySet().stream().min(LocalDate::compareTo).orElse(null);
        LocalDate max = pesos.keySet().stream().max(LocalDate::compareTo).orElse(null);
        if (min != null && max != null) {
            Double primero = pesos.get(min);
            Double ultimo  = pesos.get(max);
            if (primero != null && ultimo != null) {
                double diff = Math.round((ultimo - primero) * 10.0) / 10.0;
                ResultadoLbl.setText(
                        diff > 0 ? "Has subido " + diff + " kg en este periodo."
                                : diff < 0 ? "Has bajado " + Math.abs(diff) + " kg en este periodo."
                                : "No hay cambios en tu peso."
                );
            } else {
                ResultadoLbl.setText("Datos incompletos para calcular.");
            }
        } else {
            ResultadoLbl.setText("Datos insuficientes.");
        }
    }

    private void dibujarGrafica(Map<LocalDate, Double> pesos) {
        pesoChart.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        pesos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Double v = entry.getValue();
                    if (v != null && v > 0.01) {
                        String tick = entry.getKey().toString().substring(5);
                        serie.getData().add(new XYChart.Data<>(tick, v));
                    }
                });
        pesoChart.getData().add(serie);
        yAxis.setAutoRanging(true);
    }
    private static Map<LocalDate, Double> parsePesos(String json) {
        Map<LocalDate, Double> mapa = new HashMap<>();
        if (json == null || json.isBlank()) return mapa;
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String fechaStr = obj.optString("fecha", null);
                double peso = obj.optDouble("peso", -1);
                if (fechaStr != null && peso >= 0) {
                    LocalDate fecha = LocalDate.parse(fechaStr);
                    mapa.put(fecha, peso);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear JSON de pesos: " + e.getMessage());
        }
        return mapa;
    }

    @FXML
    private void onBackToProgresoClick(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/org/example/fitwin/progreso-view.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
