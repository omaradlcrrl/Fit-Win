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



public class ProgresoMController {

    @FXML
    private Button VolverBtn;

    @FXML
    private MenuButton metricMenu;

    @FXML
    private DatePicker desdePicker;

    @FXML
    private DatePicker hastaPicker;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Label resumenLbl;

    private Integer usuarioId;

    private enum Metrica {
        BRAZO("Brazo (cm)", "brazo", "cm"),
        PECHO("Pecho (cm)", "pecho", "cm"),
        ESPALDA("Espalda (cm)", "espalda", "cm"),
        HOMBRO("Hombro (cm)", "hombro", "cm"),
        MUSLO("Muslo (cm)", "muslo", "cm");
        final String titulo, campo, unidad;
        Metrica(String t, String c, String u){ titulo=t; campo=c; unidad=u; }
    }
    private Metrica metrica = Metrica.BRAZO;

    @FXML
    private void initialize() {
        usuarioId = Optional.ofNullable(SessionControl.getInstance().getUsuarioId()).orElse(-1);
        desdePicker.setValue(LocalDate.now().minusDays(30));
        hastaPicker.setValue(LocalDate.now());

        actualizarVista();
    }

    @FXML private void onMetricBrazo()   { setMetrica(Metrica.BRAZO); }
    @FXML private void onMetricPecho()   { setMetrica(Metrica.PECHO); }
    @FXML private void onMetricEspalda() { setMetrica(Metrica.ESPALDA); }
    @FXML private void onMetricHombro()  { setMetrica(Metrica.HOMBRO); }
    @FXML private void onMetricMuslo()   { setMetrica(Metrica.MUSLO); }
    private void setMetrica(Metrica m){ this.metrica=m; metricMenu.setText(m.titulo); actualizarVista(); }

    @FXML private void onCalcularClick(){ actualizarVista(); }

    private void actualizarVista(){
        LocalDate desde = desdePicker.getValue();
        LocalDate hasta = hastaPicker.getValue();
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            resumenLbl.setText("Selecciona un rango válido.");
            lineChart.getData().clear();
            return;
        }

        String json = ApiClient.getMedicionesRango(usuarioId, desde, hasta);
        Map<LocalDate, Double> valores = parseValores(json, metrica.campo);

        if (valores.isEmpty()) {
            lineChart.getData().clear();
            resumenLbl.setText("Sin datos en el rango");
            return;
        }

        dibujar(valores);
        resumenLbl.setText(resumenPrimerUltimo(valores, metrica.unidad));
    }

    private void dibujar(Map<LocalDate, Double> mapa){
        lineChart.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        mapa.entrySet().stream()
                .filter(e -> e.getKey()!=null && e.getValue()!=null)
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    Double valor = e.getValue();
                    if (valor > 0.01) {
                        String tick = e.getKey().toString().substring(5);
                        serie.getData().add(new XYChart.Data<>(tick, valor));
                    }
                });
        lineChart.getData().add(serie);
        yAxis.setAutoRanging(true);
    }

    private static Map<LocalDate, Double> parseValores(String json, String campo) {
        Map<LocalDate, Double> out = new HashMap<>();
        if (json == null || json.isBlank()) return out;
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                String fechaStr = obj.optString("fecha", null);
                double valor = obj.optDouble(campo, -9999);
                if (fechaStr != null && valor != -9999) {
                    LocalDate fecha = LocalDate.parse(fechaStr);
                    out.put(fecha, valor);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear JSON de medidas (" + campo + "): " + e.getMessage());
        }
        return out;
    }

    private String resumenPrimerUltimo(Map<LocalDate, Double> mapa, String unidad){
        if (mapa.isEmpty()) return "Sin datos en el rango";
        LocalDate primero = mapa.keySet().stream().min(LocalDate::compareTo).orElse(null);
        LocalDate ultimo  = mapa.keySet().stream().max(LocalDate::compareTo).orElse(null);
        if (primero == null || ultimo == null) return "Datos incompletos";
        Double v1 = mapa.get(primero);
        Double v2 = mapa.get(ultimo);
        if (v1 == null || v2 == null) return "Datos incompletos";

        double diff = Math.round((v2 - v1) * 10.0) / 10.0;
        if (diff > 0) return "Has subido " + diff + " " + unidad + " en este periodo";
        if (diff < 0) return "Has bajado " + Math.abs(diff) + " " + unidad + " en este periodo";
        return "Sin cambios en este periodo";
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
