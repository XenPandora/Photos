package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import photos.model.DataManager;
import photos.model.User;

import java.io.IOException;

/**
 * Controller for the admin panel screen.
 * Handles user management (list, create, delete users).
 * 
 * @author Your Name
 */
public class AdminController {
    @FXML
    private ListView<User> userListView;
    
    @FXML
    private Button createUserButton;
    
    @FXML
    private Button deleteUserButton;
    
    @FXML
    private Button logoutButton;
    
    private DataManager dataManager;
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        userListView.getItems().setAll(dataManager.getUsers());
        
        // Enable/disable delete button based on selection
        userListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean hasSelection = newValue != null;
                // Don't allow deleting admin user
                deleteUserButton.setDisable(!hasSelection || "admin".equals(newValue.getUsername()));
            }
        );
    }
    
    @FXML
    private void handleCreateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create User");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter username:");
        
        dialog.showAndWait().ifPresent(username -> {
            if (username.trim().isEmpty()) {
                showAlert("Error", "Username cannot be empty");
                return;
            }
            
            if (dataManager.getUser(username.trim()) != null) {
                showAlert("Error", "User already exists");
                return;
            }
            
            User newUser = new User(username.trim());
            dataManager.addUser(newUser);
            userListView.getItems().setAll(dataManager.getUsers());
            showAlert("Success", "User created successfully");
        });
    }
    
    @FXML
    private void handleDeleteUser() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null || "admin".equals(selectedUser.getUsername())) {
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete user '" + selectedUser.getUsername() + "'?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dataManager.getUsers().remove(selectedUser);
            dataManager.saveUsers();
            userListView.getItems().setAll(dataManager.getUsers());
        }
    }
    
    @FXML
    private void handleLogout() {
        dataManager.saveUsers();
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            stage.setTitle("Photos Application - Login");
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
