package com.example.photomanager.controller;

import com.example.photomanager.Application;
import com.example.photomanager.model.StorageManager;
import com.example.photomanager.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AdminController {
    @FXML
    private ListView<String> usersListView;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button createUserButton;
    @FXML
    private Button deleteUserButton;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem quitMenuItem;

    private StorageManager storageManager;
    private ObservableList<String> userNames;

    /**
     * Initialization method
     * Initializes the observable list for user names and binds it to the list view
     */
    @FXML
    public void initialize() {
        userNames = FXCollections.observableArrayList();
        usersListView.setItems(userNames);
    }

    /**
     * Sets the storage manager and refreshes the user list display
     * @param storageManager The storage manager instance
     */
    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
        refreshUserList();
    }

    /**
     * Refreshes the user list view with the latest user data
     * Appends [Admin] label to administrator accounts for distinction
     */
    private void refreshUserList() {
        userNames.clear();
        List<User> users = storageManager.getAllUsers();
        for (User user : users) {
            userNames.add(user.getUsername() + (user.isAdmin() ? " [Admin]" : ""));
        }
    }

    /**
     * Handles the create user button click event
     * Validates input and creates a new non-admin user if the username is unique
     * @param event The action event trigger
     */
    @FXML
    public void handleCreateUser(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username and password cannot be empty");
            return;
        }

        if (storageManager.findUser(username) != null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists");
            return;
        }

        User newUser = storageManager.createUser(username, password);
        if (newUser != null) {
            refreshUserList();
            usernameField.clear();
            passwordField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Success", "User created successfully");
        }
    }

    /**
     * Handles the delete user button click event
     * Shows confirmation dialog and deletes the selected non-admin user if confirmed
     * @param event The action event trigger
     */
    @FXML
    public void handleDeleteUser(ActionEvent event) {
        String selected = usersListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a user to delete");
            return;
        }

        // Extract pure username (remove [Admin] label)
        String username = selected.replace(" [Admin]", "").trim();

        // Prevent deletion of admin user
        User user = storageManager.findUser(username);
        if (user != null && user.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete admin user");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete user '" + username + "'?");
        alert.showAndWait().ifPresent(response -> {
            if (storageManager.deleteUser(username)) {
                refreshUserList();
                showAlert(Alert.AlertType.INFORMATION, "Success", "User has been deleted");
            }
        });
    }

    /**
     * Handles the logout action
     * Navigates back to the login screen
     * @param event The action event trigger
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) logoutMenuItem.getParentPopup().getOwnerWindow();
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 400, 300);
            stage.setScene(scene);
            stage.setTitle("Photo Manager - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to logout");
        }
    }

    /**
     * Handles the quit action
     * Closes the application window
     * @param event The action event trigger
     */
    @FXML
    public void handleQuit(ActionEvent event) {
        Stage stage = (Stage) quitMenuItem.getParentPopup().getOwnerWindow();
        stage.close();
    }

    /**
     * Displays a standard alert dialog
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
