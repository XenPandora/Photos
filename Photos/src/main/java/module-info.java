module com.example.photo_manager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.photomanager.controller to javafx.fxml;
    opens com.example.photomanager to javafx.fxml;
    exports com.example.photomanager;
    exports com.example.photomanager.controller;


    requires javafx.graphics;
}
