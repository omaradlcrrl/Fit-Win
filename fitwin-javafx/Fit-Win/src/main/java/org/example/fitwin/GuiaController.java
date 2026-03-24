package org.example.fitwin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GuiaController {

    @FXML
    private VBox ContenedorImg;

    @FXML
    private ScrollPane GuiaScroll;

    @FXML
    private TextField BuscarTxt;


    private static final String IMAGES_DIRECTORY = "Fit-Win/src/main/resources/imagenes/";

    @FXML
    public void initialize() {
        GuiaScroll.setFitToHeight(true);
        GuiaScroll.setFitToWidth(true);

        BuscarTxt.textProperty().addListener((observable, oldText, newText) -> buscarEjercicio());

        buscarEjercicio();
    }

    private void buscarEjercicio() {

        String searchText = BuscarTxt.getText() == null
                ? ""
                : BuscarTxt.getText().trim().toLowerCase();
        ContenedorImg.getChildren().clear();

        File imagesFolder = new File(IMAGES_DIRECTORY);
        File[] imageFiles = imagesFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (imageFiles == null || imageFiles.length == 0) {
            return;
        }

        List<File> filteredImages = searchText.isEmpty()
                ? List.of(imageFiles)
                : List.of(imageFiles).stream()
                .filter(file -> file.getName().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        for (File imageFile : filteredImages) {
            ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);

            ContenedorImg.getChildren().add(imageView);
        }
    }

    @FXML
    private void onBackToEjercicioClick(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/org/example/fitwin/ejercicio-view.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la vista: ejercicio-view.fxml");
        }
    }
}
