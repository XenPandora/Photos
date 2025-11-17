package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.DataManager;
import photos.model.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the user dashboard screen.
 * Handles album management and navigation to search/album views.
 * 
 * @author Your Name
 */
public class UserDashboardController {
    @FXML
    private ListView<Album> albumListView;
    
    @FXML
    private Button createAlbumButton;
    
    @FXML
    private Button deleteAlbumButton;
    
    @FXML
    private Button renameAlbumButton;
    
    @FXML
    private Button openAlbumButton;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button manageTagTypesButton;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private TextField albumNameField;
    
    private DataManager dataManager;
    private User currentUser;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        currentUser = dataManager.getCurrentUser();
        
        if (currentUser != null) {
            albumListView.getItems().setAll(currentUser.getAlbums());
        }
        
        // Set custom cell factory to display album info
        albumListView.setCellFactory(listView -> new ListCell<Album>() {
            @Override
            protected void updateItem(Album album, boolean empty) {
                super.updateItem(album, empty);
                if (empty || album == null) {
                    setText(null);
                } else {
                    StringBuilder text = new StringBuilder(album.getName());
                    text.append(" (").append(album.getPhotoCount()).append(" photos");
                    
                    if (album.getPhotoCount() > 0) {
                        text.append(", ");
                        if (album.getEarliestDate() != null && album.getLatestDate() != null) {
                            if (album.getEarliestDate().equals(album.getLatestDate())) {
                                text.append(album.getEarliestDate().format(DATE_FORMATTER));
                            } else {
                                text.append(album.getEarliestDate().format(DATE_FORMATTER))
                                    .append(" to ")
                                    .append(album.getLatestDate().format(DATE_FORMATTER));
                            }
                        }
                    }
                    text.append(")");
                    setText(text.toString());
                }
            }
        });
        
        // Enable/disable buttons based on selection
        albumListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean hasSelection = newValue != null;
                deleteAlbumButton.setDisable(!hasSelection);
                renameAlbumButton.setDisable(!hasSelection);
                openAlbumButton.setDisable(!hasSelection);
            }
        );
    }
    
    @FXML
    private void handleCreateAlbum() {
        String albumName = albumNameField.getText().trim();
        
        if (albumName.isEmpty()) {
            showAlert("Error", "Please enter an album name");
            return;
        }
        
        if (currentUser.getAlbumByName(albumName) != null) {
            showAlert("Error", "Album already exists");
            return;
        }
        
        Album album = new Album(albumName);
        currentUser.addAlbum(album);
        dataManager.saveUsers();
        albumListView.getItems().setAll(currentUser.getAlbums());
        albumNameField.clear();
    }
    
    @FXML
    private void handleDeleteAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Album");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete '" + selectedAlbum.getName() + "'?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            currentUser.removeAlbum(selectedAlbum);
            dataManager.saveUsers();
            albumListView.getItems().setAll(currentUser.getAlbums());
        }
    }
    
    @FXML
    private void handleRenameAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog(selectedAlbum.getName());
        dialog.setTitle("Rename Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new album name:");
        
        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty() && currentUser.getAlbumByName(newName.trim()) == null) {
                selectedAlbum.setName(newName.trim());
                dataManager.saveUsers();
                albumListView.refresh();
            } else if (currentUser.getAlbumByName(newName.trim()) != null) {
                showAlert("Error", "Album name already exists");
            }
        });
    }
    
    @FXML
    private void handleOpenAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            return;
        }
        
        try {
            Stage stage = (Stage) openAlbumButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/fxml/album_view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 700);
            
            AlbumViewController controller = loader.getController();
            controller.setAlbum(selectedAlbum);
            
            stage.setTitle("Photos Application - " + selectedAlbum.getName());
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Error", "Failed to open album: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        try {
            Stage stage = (Stage) searchButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/fxml/search.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setTitle("Photos Application - Search");
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Error", "Failed to open search: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleManageTagTypes() {
        // Show dialog to manage tag types
        StringBuilder tagTypesText = new StringBuilder("Current Tag Types:\n\n");
        for (photos.model.TagType tagType : currentUser.getTagTypes()) {
            tagTypesText.append(tagType.getName())
                       .append(" - ")
                       .append(tagType.allowsMultipleValues() ? "Multiple values" : "Single value")
                       .append("\n");
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Manage Tag Types");
        dialog.setHeaderText(tagTypesText.toString());
        dialog.setContentText("Enter new tag type name (or cancel to close):");
        
        dialog.showAndWait().ifPresent(tagTypeName -> {
            if (!tagTypeName.trim().isEmpty()) {
                if (currentUser.getTagTypeByName(tagTypeName.trim()) != null) {
                    showAlert("Error", "Tag type already exists");
                    return;
                }
                
                // Ask if multiple values are allowed
                Alert multipleDialog = new Alert(Alert.AlertType.CONFIRMATION);
                multipleDialog.setTitle("Tag Type Options");
                multipleDialog.setHeaderText(null);
                multipleDialog.setContentText("Allow multiple values for this tag type?");
                multipleDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                
                multipleDialog.showAndWait().ifPresent(buttonType -> {
                    boolean allowsMultiple = buttonType == ButtonType.YES;
                    photos.model.TagType newTagType = new photos.model.TagType(tagTypeName.trim(), allowsMultiple);
                    currentUser.addTagType(newTagType);
                    dataManager.saveUsers();
                    showAlert("Success", "Tag type added successfully");
                });
            }
        });
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
