package org.example.fitwin;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.fitwin.util.ApiClient;
import org.example.fitwin.util.SessionControl;
import org.json.JSONObject;


public class LoginController {

    @FXML
    private TextField EmailTxt;

    @FXML
    private PasswordField PassTxt;

    @FXML
    private Label EstadoLbl;

    @FXML
    private Button LoginBtn;

    @FXML
    void onLoginButtonClick(ActionEvent event) {
        String email = EmailTxt.getText() == null ? "" : EmailTxt.getText().trim().toLowerCase();
        String password = PassTxt.getText() == null ? "" : PassTxt.getText();
        if (email.isEmpty() || password.isEmpty()) {
            showError("Rellena email y contraseña.");
            return;
        }
        try {
            disableWhile(true);
            String loginJson = """
                {
                  "correoElectronico": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);
            var response = ApiClient.loginUser(loginJson);
            if (response.statusCode() == 200) {
                JSONObject obj = new JSONObject(response.body());
                int usuarioId = obj.optInt("usuarioId", -1);
                if (usuarioId > 0) {
                    SessionControl.getInstance().setUsuarioId(usuarioId);
                    showError("Inicio de sesión correcto.");
                    goTo("/org/example/fitwin/menu-view.fxml", event);
                } else {
                    showError("Respuesta sin usuarioId.");
                }
            } else if (response.statusCode() >= 400 && response.statusCode() < 500) {
                showError("Correo o contraseña incorrectos.");
            } else {
                showError("Error del servidor (" + response.statusCode() + ").");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al conectar.");
        }
        finally {
            disableWhile(false);
        }
    }

    @FXML
    public void onRegisterButtonClick(ActionEvent event) {
        try {
            goTo("/org/example/fitwin/register-view.fxml", event);
        } catch (Exception e) {
            e.printStackTrace();
            showError("No se pudo abrir el registro.");
        }
    }

    private void goTo(String fxmlPath, ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    private void showError(String msg) {
        if (EstadoLbl != null) {
            EstadoLbl.setText(msg);
            EstadoLbl.setVisible(true);
            EstadoLbl.setManaged(true);
            PauseTransition t = new PauseTransition(Duration.seconds(3));
            t.setOnFinished(ev -> {
                EstadoLbl.setVisible(false);
                EstadoLbl.setManaged(false);
            });
            t.play();
        } else {
            System.out.println("[ESTADO] " + msg);
        }
    }

    private void disableWhile(boolean busy) {
        if (LoginBtn != null) LoginBtn.setDisable(busy);

    }
}
