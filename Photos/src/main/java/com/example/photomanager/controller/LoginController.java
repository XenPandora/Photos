package com.example.photomanager.controller;

import com.example.photomanager.Application;
import com.example.photomanager.model.StorageManager;
import com.example.photomanager.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private StorageManager storageManager;

    /**
     * Initializes the controller.
     * Creates a new StorageManager instance to handle user data.
     */
    @FXML
    public void initialize() {
        storageManager = new StorageManager();
    }

    /**
     * Handles the login action.
     * Validates user credentials and loads the appropriate interface (admin or user).
     * @param event The action event trigger
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Please enter username");
            return;
        }

        User user = storageManager.findUser(username);
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "User does not exist");
            return;
        }

        // Validate password (passwords for 'stock' and 'admin' are preset in StorageManager)
        if (!user.getPassword().equals(password)) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect password");
            return;
        }

        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root;

            // Load different interfaces based on user type
            if (user.isAdmin()) {
                FXMLLoader loader = new FXMLLoader(Application.class.getResource("admin.fxml"));
                root = loader.load();
                AdminController controller = loader.getController();
                controller.setStorageManager(storageManager);
            } else {
                FXMLLoader loader = new FXMLLoader(Application.class.getResource("user.fxml"));
                root = loader.load();
                UserController controller = loader.getController();
                controller.setUser(user);
                controller.setStorageManager(storageManager);
            }

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Photo Manager - " + username);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load interface");
        }
    }

    /**
     * Handles the quit action.
     * Closes the login window and exits the application.
     * @param event The action event trigger
     */
    @FXML
    public void handleQuit(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays a standard alert dialog.
     * @param type The alert type (information, error, confirmation, etc.)
     * @param title The alert dialog title
     * @param message The alert content message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
