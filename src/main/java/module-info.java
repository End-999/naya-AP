module org.example.cleanprj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens org.example.cleanprj to javafx.fxml;
    opens org.example.cleanprj.controllers to javafx.fxml;
    opens org.example.cleanprj.models to javafx.base;

    exports org.example.cleanprj;
    exports org.example.cleanprj.controllers;
    exports org.example.cleanprj.models;
}
