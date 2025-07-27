package org.example.cleanprj;

//ADMIN_001,Sahel Admin,saheladmin123@gmail.com,sahel@123,Nepali,Admin,2025-07-26T20:06:20.505204,
//ADMIN_002,Nupun Admin,nupunadmin123@gmail.com,nupun@123,Nepali,Admin,2025-07-26T20:06:20.505222,
//ADMIN_003,System Admin,admin123@gmail.com,admin@123,Nepali,Admin,2025-07-26T20:06:20.505227,

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/admin-dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 850);

        // Load CSS with proper path
        String css = this.getClass().getResource("styles/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Nepal Tourism Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
