package org.example.fitwin;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import java.io.IOException;

public class EjercicioController {

    @FXML
    private TextField NombreTxt;

    @FXML
    private TextField PosicionTxt;

    @FXML
    private ComboBox<String> DiaCombo;

    @FXML
    private TextField SeriesTxt;

    @FXML
    private TextField RepeticionesTxt;

    @FXML
    private Label EstadoLbl;

    @FXML
    private void initialize() {
        DiaCombo.getItems().addAll("Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo");
    }

    @FXML
    private void onSaveEjercicioButtonClick(ActionEvent event) {
        if (!validateBaseFields()) {
            showEstado("Completa nombre, dia, series y repeticiones.", true);
            return;
        }

        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) { showEstado("Usuario no autenticado. Inicia sesion.", true); return; }

        String nombre = NombreTxt.getText().trim();
        String dia    = DiaCombo.getValue();
        String posTxt = PosicionTxt.getText();

        Integer series, reps, posicion = null;
        try {
            series = Integer.parseInt(SeriesTxt.getText().trim());
            reps   = Integer.parseInt(RepeticionesTxt.getText().trim());
            if (posTxt != null && !posTxt.isBlank()) {
                posicion = Integer.parseInt(posTxt.trim());
                if (posicion <= 0) { showEstado("La posicion debe ser mayor que 0.", true); return; }
            }
        } catch (NumberFormatException nfe) {
            showEstado("Series/Repeticiones/Posicion deben ser numeros enteros.", true);
            return;
        }

        try {
            if (posicion != null) {
                showEstado("Modificar posicion no soportado.", true);
            } else {
                String jsonBody = """
                    {
                      "nombre": "%s",
                      "diaSemana": "%s",
                      "series": %d,
                      "repeticiones": %d,
                      "usuarioId": %d
                    }
                    """.formatted(nombre, dia, series, reps, usuarioId);

                var resp = ApiClient.saveEjercicio(jsonBody);
                if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                    showEstado("Ejercicio guardado exitosamente.", false);
                    clearFields();
                } else {
                    showEstado("Error al guardar (" + resp.statusCode() + ").", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEstado("Error de red: " + e.getMessage(), true);
        }
    }

    @FXML
    private void onDeleteEjercicioButtonClick(ActionEvent event) {
        Integer usuarioId = SessionControl.getInstance().getUsuarioId();
        if (usuarioId == null) { showEstado("Usuario no autenticado.", true); return; }

        String dia = DiaCombo.getValue();
        String posTxt = PosicionTxt.getText();
        String nombre = NombreTxt.getText();

        if (dia == null || dia.isBlank()) { showEstado("Selecciona el dia para borrar.", true); return; }

        try {
            if (posTxt != null && !posTxt.isBlank()) {
                int posicion = Integer.parseInt(posTxt.trim());
                if (posicion <= 0) { showEstado("La posicion debe ser mayor que 0.", true); return; }

                var resp = ApiClient.deleteEjercicioByPosicionYDia(usuarioId, dia, posicion);
                if (resp.statusCode() == 200) {
                    showEstado("Ejercicio borrado.", false);
                    clearFields();
                } else if (resp.statusCode() == 404) {
                    showEstado("No existe ejercicio en ese dia/posicion.", true);
                } else {
                    showEstado("Error al borrar (" + resp.statusCode() + ").", true);
                }
            } else if (nombre != null && !nombre.isBlank()) {
                showEstado("Borrar por nombre no soportado.", true);
            } else {
                showEstado("Indica posicion.", true);
            }
        } catch (NumberFormatException nfe) {
            showEstado("La posicion debe ser un numero.", true);
        } catch (Exception e) {
            e.printStackTrace();
            showEstado("Error de red: " + e.getMessage(), true);
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
    private void onGuiaButtonClick(ActionEvent event) {
        try {
            Parent guiaViewParent = FXMLLoader.load(getClass().getResource("guia-view.fxml"));
            Scene guiaViewScene = new Scene(guiaViewParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(guiaViewScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            showEstado("No se pudo cargar la guia.", true);
        }
    }

    @FXML
    private void onRutinaDeHoyButtonClick(ActionEvent event) {
        try {
            Parent rutinaViewParent = FXMLLoader.load(getClass().getResource("tabla-view.fxml"));
            Scene rutinaViewScene = new Scene(rutinaViewParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(rutinaViewScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            showEstado("No se pudo cargar la rutina.", true);
        }
    }

    private boolean validateBaseFields() {
        return !(NombreTxt.getText().isEmpty()
                || DiaCombo.getValue() == null
                || SeriesTxt.getText().isEmpty()
                || RepeticionesTxt.getText().isEmpty());
    }

    private void clearFields() {
        NombreTxt.clear();
        PosicionTxt.clear();
        DiaCombo.getSelectionModel().clearSelection();
        SeriesTxt.clear();
        RepeticionesTxt.clear();
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