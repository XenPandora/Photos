package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos.model.DataManager;
import photos.model.User;

import java.io.IOException;

/**
 * Controller for the login screen.
 * Handles user authentication and navigation to admin or user dashboard.
 * 
 * @author Your Name
 */
public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private Button loginButton;
    
    private DataManager dataManager;
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showAlert("Error", "Please enter a username");
            return;
        }
        
        User user = dataManager.getUser(username);
        if (user == null) {
            // Create new user
            user = new User(username);
            dataManager.addUser(user);
        }
        
        dataManager.setCurrentUser(user);
        
        // Navigate to appropriate screen
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader;
            
            if (username.equals("admin")) {
                loader = new FXMLLoader(getClass().getResource("/photos/view/fxml/admin.fxml"));
                stage.setTitle("Photos Application - Admin");
            } else {
                loader = new FXMLLoader(getClass().getResource("/photos/view/fxml/user_dashboard.fxml"));
                stage.setTitle("Photos Application - " + username);
            }
            
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Error", "Failed to load dashboard: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
