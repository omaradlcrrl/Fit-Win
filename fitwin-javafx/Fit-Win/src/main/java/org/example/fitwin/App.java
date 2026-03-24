package org.example.fitwin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 400, 800);
        stage.setTitle("Fit-Win");
        stage.setScene(scene);

        stage.setWidth(400);
        stage.setHeight(800);
        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
