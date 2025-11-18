package com.example.photomanager.controller;

import com.example.photomanager.Application;
import com.example.photomanager.model.Album;
import com.example.photomanager.model.StorageManager;
import com.example.photomanager.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class UserController {
    @FXML
    private ListView<Album> albumsListView;
    @FXML
    private Button createAlbumButton;
    @FXML
    private Button deleteAlbumButton;
    @FXML
    private Button renameAlbumButton;
    @FXML
    private Button openAlbumButton;
    @FXML
    private TextField renameField;
    @FXML
    private Label albumInfoLabel;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private MenuItem searchMenuItem;

    private User currentUser;
    private StorageManager storageManager;
    private ObservableList<Album> albums;

    /**
     * Initializes the controller.
     * Sets up the album list view with custom cell formatting and selection listener.
     */
    @FXML
    public void initialize() {
        albums = FXCollections.observableArrayList();
        albumsListView.setItems(albums);

        // Customize album display format in the list view
        albumsListView.setCellFactory(param -> new ListCell<Album>() {
            @Override
            protected void updateItem(Album item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String info = String.format("%s (%d photos)  %s - %s",
                            item.getName(),
                            item.getPhotoCount(),
                            item.getEarliestDate() != null ? sdf.format(item.getEarliestDate()) : "None",
                            item.getLatestDate() != null ? sdf.format(item.getLatestDate()) : "None");
                    setText(info);
                }
            }
        });

        // Update album info when a different album is selected
        albumsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                updateAlbumInfo(newVal);
            }
        });
    }

    /**
     * Sets the current logged-in user and loads their albums into the list.
     * @param user The current user to set
     */
    public void setUser(User user) {
        this.currentUser = user;
        albums.clear();
        albums.addAll(user.getAlbums());
    }

    /**
     * Sets the storage manager for data persistence.
     * @param storageManager The storage manager instance
     */
    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * Updates the album information label with details of the selected album.
     * @param album The album to display information for
     */
    private void updateAlbumInfo(Album album) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder info = new StringBuilder();
        info.append("Album Name: ").append(album.getName()).append("\n");
        info.append("Photo Count: ").append(album.getPhotoCount()).append("\n");
        info.append("Earliest Date: ").append(album.getEarliestDate() != null ? sdf.format(album.getEarliestDate()) : "None").append("\n");
        info.append("Latest Date: ").append(album.getLatestDate() != null ? sdf.format(album.getLatestDate()) : "None");
        albumInfoLabel.setText(info.toString());
    }

    /**
     * Handles the create album action.
     * Shows a dialog to input album name and creates the album if the name is unique.
     * @param event The action event trigger
     */
    @FXML
    public void handleCreateAlbum(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("New Album");
        dialog.setTitle("Create Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter album name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                Album album = currentUser.createAlbum(name.trim());
                if (album != null) {
                    albums.add(album);
                    storageManager.saveUsers();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Album created successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Album name already exists");
                }
            }
        });
    }

    /**
     * Handles the delete album action.
     * Shows a confirmation dialog and deletes the selected album if confirmed.
     * @param event The action event trigger
     */
    @FXML
    public void handleDeleteAlbum(ActionEvent event) {
        Album selected = albumsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an album to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete the album '" + selected.getName() + "'?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                currentUser.deleteAlbum(selected);
                albums.remove(selected);
                storageManager.saveUsers();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Album deleted successfully");
            }
        });
    }

    /**
     * Handles the rename album action.
     * Renames the selected album with the new name from the text field (if valid).
     * @param event The action event trigger
     */
    @FXML
    public void handleRenameAlbum(ActionEvent event) {
        Album selected = albumsListView.getSelectionModel().getSelectedItem();
        String newName = renameField.getText().trim();

        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an album to rename");
            return;
        }

        if (newName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a new name");
            return;
        }

        if (currentUser.findAlbum(newName) != null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Name already exists");
            return;
        }

        selected.setName(newName);
        renameField.clear();
        albumsListView.refresh();
        storageManager.saveUsers();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Album renamed successfully");
    }

    /**
     * Handles the open album action.
     * Loads the album view and passes the selected album to the AlbumController.
     * @param event The action event trigger
     */
    @FXML
    public void handleOpenAlbum(ActionEvent event) {
        Album selected = albumsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an album to open");
            return;
        }

        try {
            Stage stage = (Stage) openAlbumButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("album.fxml"));
            Parent root = loader.load();

            AlbumController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setAlbum(selected);
            controller.setStorageManager(storageManager);

            Scene scene = new Scene(root, 1000, 800);
            stage.setScene(scene);
            stage.setTitle("Album - " + selected.getName());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open album");
        }
    }

    /**
     * Handles the logout action.
     * Navigates back to the login screen.
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
     * Handles the quit action.
     * Closes the application window.
     * @param event The action event trigger
     */
    @FXML
    public void handleQuit(ActionEvent event) {
        Stage stage = (Stage) quitMenuItem.getParentPopup().getOwnerWindow();
        stage.close();
    }

    /**
     * Handles the search action.
     * Loads the search view for photo filtering.
     * @param event The action event trigger
     */
    @FXML
    public void handleSearch(ActionEvent event) {
        try {
            Stage stage = (Stage) searchMenuItem.getParentPopup().getOwnerWindow();
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("search.fxml"));
            Parent root = loader.load();

            SearchController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setStorageManager(storageManager);

            Scene scene = new Scene(root, 1000, 800);
            stage.setScene(scene);
            stage.setTitle("Search Photos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open search interface");
        }
    }

    /**
     * Displays a standard alert dialog with the specified type, title, and message.
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
