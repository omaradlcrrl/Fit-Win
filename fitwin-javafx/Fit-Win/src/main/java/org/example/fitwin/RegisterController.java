package org.example.fitwin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.fitwin.util.ApiClient;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegisterController {

    @FXML
    private TextField NomTxt;

    @FXML
    private TextField ApellTxt;

    @FXML
    private TextField EmailTxt;

    @FXML
    private TextField PassTxt;

    @FXML
    private TextField AltuTxt;

    @FXML
    private TextField IdiomTxt;

    @FXML
    private DatePicker FNacimientoPicker;

    @FXML
    private ComboBox<String> EstratCombo;

    @FXML
    private Label EstadoLbl;

    @FXML
    private Label EstratEtq;



    @FXML
    public void initialize() {
        EstratCombo.getItems().addAll("MANTENIMIENTO", "SUPERAVIT", "DEFICIT");
        EstratCombo.getSelectionModel().select("MANTENIMIENTO");
        updateEstrategiaMsg("MANTENIMIENTO");

        EstratCombo.valueProperty().addListener((obs, o, n) -> {
            if (n != null) updateEstrategiaMsg(n);
        });
    }


    @FXML
    void onRegisterButtonClick(ActionEvent event) {
        String nombre    = t(NomTxt.getText());
        String apellidos = t(ApellTxt.getText());
        String email     = t(EmailTxt.getText()).toLowerCase();
        String password  = t(PassTxt.getText());
        String alturaS   = t(AltuTxt.getText());
        String idioma    = t(IdiomTxt.getText());
        String estrategia= EstratCombo.getValue();
        LocalDate fecha = FNacimientoPicker.getValue();
        if(EstadoLbl != null) {
            EstadoLbl.setVisible(false);
            EstadoLbl.setManaged(false);
        }
        if (nombre.isEmpty() || password.isEmpty() || email.isEmpty()
                || alturaS.isEmpty() || idioma.isEmpty() || fecha == null) {
            showError("Por favor, rellena todos los campos.");
            return;
        }
        try {
            String alturaNorm = alturaS.replace(",", ".");
            double altura = Double.parseDouble(alturaNorm);
            String fechaISO = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
            JSONObject json = new JSONObject();
            json.put("nombre", nombre);
            json.put("apellidos", apellidos);
            json.put("password", password);
            json.put("correoElectronico", email);
            json.put("altura", altura);
            json.put("idioma", idioma);
            json.put("fechaNacimiento", fechaISO);
            json.put("estrategia", estrategia);
            var response = ApiClient.registerUser(json.toString());
            if (response.statusCode() == 201) {
                System.out.println("Usuario registrado con éxito.");
                onBackButtonClick(event);
            } else {
                showError("Error: El usuario ya existe o datos inválidos.");
            }
        } catch (NumberFormatException e) {
            showError("La altura debe ser un número (ej. 1.75).");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error de conexión/servidor.");
        }
    }
    private void showError(String msg) {
        if(EstadoLbl != null) {
            EstadoLbl.setText(msg);
            EstadoLbl.setVisible(true);
            EstadoLbl.setManaged(true);
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
            delay.setOnFinished(e -> {
                EstadoLbl.setVisible(false);
                EstadoLbl.setManaged(false);
            });
            delay.play();
        } else {
            System.out.println(msg);
        }
    }

    private void updateEstrategiaMsg(String estrategia) {
        switch (estrategia) {
            case "SUPERAVIT" -> EstratEtq.setText("Objetivo: subir masa.");
            case "DEFICIT"   -> EstratEtq.setText("Objetivo: perder grasa.");
            default          -> EstratEtq.setText("Objetivo: mantener peso.");
        }
    }

    private String t(String s) {
        return s == null ? "" : s.trim();
    }

    @FXML
    void onBackButtonClick(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/org/example/fitwin/login-view.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo abrir la pantalla de login.");
        }
    }
}
