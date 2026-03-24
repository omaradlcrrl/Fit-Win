package org.example.fitwin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.fitwin.model.Ejercicio;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class TableController {

    @FXML
    private TableView<Ejercicio> TablaEjercicios;

    @FXML
    private TableColumn<Ejercicio, String> colEjercicio;

    @FXML
    private TableColumn<Ejercicio, Integer> colSeries;

    @FXML
    private TableColumn<Ejercicio, Integer> colReps;

    @FXML
    private ComboBox<String> DiaCombo;

    private ObservableList<Ejercicio> ejerciciosList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        colEjercicio.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSeries.setCellValueFactory(new PropertyValueFactory<>("series"));
        colReps.setCellValueFactory(new PropertyValueFactory<>("repeticiones"));

        DiaCombo.getItems().addAll("Lunes", "Martes", "Miércoles", "Jueves",
                "Viernes", "Sábado", "Domingo");
        DiaCombo.getSelectionModel().selectFirst();

        cargarEjerciciosPorDia();
        DiaCombo.setOnAction(event -> cargarEjerciciosPorDia());

        TablaEjercicios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colEjercicio.setResizable(false);
        colSeries.setResizable(false);
        colReps.setResizable(false);

        colEjercicio.prefWidthProperty().bind(TablaEjercicios.widthProperty().multiply(0.5));
        colSeries.prefWidthProperty().bind(TablaEjercicios.widthProperty().multiply(0.25));
        colReps.prefWidthProperty().bind(TablaEjercicios.widthProperty().multiply(0.25));
    }

    private void cargarEjerciciosPorDia() {
        String diaSeleccionado = DiaCombo.getValue();
        if (diaSeleccionado == null) return;

        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) {
            mostrarAlerta("Error", "Usuario no autenticado. Inicia sesión.");
            return;
        }

        String responseJson = ApiClient.getEjerciciosPorDia(usuarioId, diaSeleccionado);
        List<Ejercicio> ejercicios = parsearEjercicios(responseJson);

        ejerciciosList.setAll(ejercicios);
        TablaEjercicios.setItems(ejerciciosList);
    }

    private List<Ejercicio> parsearEjercicios(String json) {
        List<Ejercicio> lista = new ArrayList<>();

        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return lista;
        }

        try {
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                String nombre = obj.optString("nombre", "");
                int series = obj.optInt("series", 0);
                int repeticiones = obj.optInt("repeticiones", 0);

                Ejercicio e = new Ejercicio(nombre, series, repeticiones);
                lista.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        System.err.println("[" + titulo + "] " + mensaje);
    }

    @FXML
    private void onBackToEjercicioClick(ActionEvent event) {
        try {
            Parent menuViewParent = FXMLLoader.load(getClass().getResource("ejercicio-view.fxml"));
            Scene menuViewScene = new Scene(menuViewParent);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(menuViewScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el menú.");
        }
    }
}
