package org.example.cleanprj.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneChanger {

    public void changeScene(String sceneName, Stage stage) throws IOException {
        // Use proper resource path for FXML files
        String fxmlPath = "/org/example/cleanprj/views/" + sceneName;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 750);

        // Load CSS
        String css = getClass().getResource("/org/example/cleanprj/styles/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle(sceneName);
        stage.setScene(scene);
        stage.show();
    }

    public void changeSceneWithController(String sceneName, Stage stage, Object controller) throws IOException {
        String fxmlPath = "/org/example/projectassignement/views/" + sceneName;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1440, 750);

        // Load CSS
        String css = getClass().getResource("/org/example/projectassignement/styles/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle(sceneName);
        stage.setScene(scene);
        stage.show();
    }
}
