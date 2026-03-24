package org.example.fitwin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.fitwin.util.SessionControl;
import java.io.IOException;

public class MenuController {

    private void cambiarVista(ActionEvent evento, String vista) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource(vista));
            Parent nuevaVista = cargador.load();

            Scene nuevaEscena = new Scene(nuevaVista);
            Stage ventana = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            ventana.setScene(nuevaEscena);
            ventana.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la vista: " + vista);
        }
    }

    @FXML
    private void alHacerClickEnComidas(ActionEvent evento) {
        cambiarVista(evento, "comida-view.fxml");
    }

    @FXML
    private void alHacerClickEnBotonPerfil(ActionEvent evento) {
        cambiarVista(evento, "perfil-view.fxml");
    }

    @FXML
    private void alHacerClickEnBotonAjustes(ActionEvent evento) {
        cambiarVista(evento,"ajustes-view.fxml");
    }

    @FXML
    private void alHacerClickEnBotonEjercicio(ActionEvent evento) {
        cambiarVista(evento, "ejercicio-view.fxml");
    }

    @FXML
    private void alHacerClickEnBotonTracker(ActionEvent evento) {
        cambiarVista(evento, "progreso-view.fxml");
    }

    @FXML
    private void alHacerClickEnBotonCerrarSesion(ActionEvent evento) {
        SessionControl.getInstance().cerrar();
        cambiarVista(evento, "login-view.fxml");
    }
    @FXML
    private void alHacerClickEnBotonPeso(ActionEvent evento) {
        cambiarVista(evento, "peso-view.fxml");
    }
    @FXML
    private void alHacerClickEnBotonMedidas(ActionEvent e){
        cambiarVista(e,"medidas-view.fxml"); }
    @FXML
    private void alHacerClickEnBotonObjetivos(ActionEvent e){
        cambiarVista(e,"objetivos-view.fxml"); }



}

