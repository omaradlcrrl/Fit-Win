package org.example.fitwin;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import org.json.JSONObject;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;




public class ModificaController {

    @FXML private TextField NombreTxt;
    @FXML private TextField ApellidosTxt;
    @FXML private TextField CorreoTxt;
    @FXML private PasswordField PasswordTxt;
    @FXML private DatePicker FechaNacimientoPicker;
    @FXML private TextField AlturaTxt;
    @FXML private Button VolverBtn;
    @FXML private Label EstadoLbl;

    private int usuarioId;

    @FXML
    public void initialize() {
        usuarioId = SessionControl.getInstance().getUsuarioId();
    }

    @FXML
    private void guardarCambios() {
        try {
            JSONObject payload = new JSONObject();

            String nombre = "";
            if (NombreTxt.getText() != null) nombre = NombreTxt.getText().trim();

            String apellidos = "";
            if (ApellidosTxt.getText() != null) apellidos = ApellidosTxt.getText().trim();

            String correo = "";
            if (CorreoTxt.getText() != null) correo = CorreoTxt.getText().trim();

            String pass = "";
            if (PasswordTxt.getText() != null) pass = PasswordTxt.getText().trim();

            LocalDate nuevaFecha = FechaNacimientoPicker.getValue();

            String altura = "";
            if (AlturaTxt.getText() != null) altura = AlturaTxt.getText().trim();

            if (nombre.isEmpty() && apellidos.isEmpty() && correo.isEmpty()
                    && pass.isEmpty() && nuevaFecha == null && altura.isEmpty()) {
                showEstado("No hay nada que guardar.");
                return;
            }

            if (nuevaFecha != null) {
                payload.put("fechaNacimiento", nuevaFecha.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            if (!altura.isEmpty()) {
                try {
                    double valorAltura = Double.parseDouble(altura.replace(",", "."));
                    if (valorAltura <= 0 || valorAltura > 3) {
                        showEstado("Altura imposible.");
                        return;
                    }
                    payload.put("altura", valorAltura);
                } catch (Exception e) {
                    showEstado("La altura tiene que ser un número.");
                    return;
                }
            }

            if (!nombre.isEmpty()) {
                payload.put("nombre", nombre);
            }
            if (!apellidos.isEmpty()) {
                payload.put("apellidos", apellidos);
            }
            if (!correo.isEmpty()) {
                payload.put("correoElectronico", correo);
            }
            if (!pass.isEmpty()) {
                payload.put("password", pass);
            }

            HttpResponse<String> r = ApiClient.updateUser(usuarioId, payload.toString());

            if (r.statusCode() == 200) {
                irAPerfil();
            } else if (r.statusCode() == 409) {
                showEstado("El correo ya existe.");
            } else if (r.statusCode() == 400) {
                showEstado("Datos incorrectos. Revisa todo.");
            } else {
                showEstado("Error al guardar.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showEstado("Fallo de conexión.");
        }
    }

    @FXML
    private void onBackToPerfilClick(ActionEvent event) {
        irAPerfil();
    }

    private void irAPerfil() {
        try {
            Parent p = FXMLLoader.load(getClass().getResource("/org/example/fitwin/perfil-view.fxml"));
            Stage s = (Stage) VolverBtn.getScene().getWindow();
            s.setScene(new Scene(p));
            s.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEstado(String msg) {
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