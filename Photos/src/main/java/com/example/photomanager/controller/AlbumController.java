package com.example.photomanager.controller;

import com.example.photomanager.Application;
import com.example.photomanager.model.Album;
import com.example.photomanager.model.Photo;
import com.example.photomanager.model.StorageManager;
import com.example.photomanager.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Album Controller handling the logic for the album interface
 */
public class AlbumController {
    @FXML
    private TilePane photosTilePane;

    @FXML
    private TextField captionField;

    @FXML
    private Button addPhotoButton;

    @FXML
    private Button removePhotoButton;

    @FXML
    private Button saveCaptionButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button backButton;

    @FXML
    private Button copyButton;

    @FXML
    private Button moveButton;

    @FXML
    private ComboBox<Album> albumComboBox;

    @FXML
    private TextField tagNameField;

    @FXML
    private TextField tagValueField;

    @FXML
    private Button addTagButton;

    @FXML
    private ListView<String> tagsListView;

    @FXML
    private Button removeTagButton;

    private User currentUser;
    private Album currentAlbum;
    private StorageManager storageManager;
    private ObservableList<Album> albums;
    private ObservableList<String> tags;
    private Photo selectedPhoto;
    private int currentIndex = 0;

    /**
     * Initialization method
     * Initializes observable lists for albums and tags, and binds them to UI components
     */
    @FXML
    public void initialize() {
        albums = FXCollections.observableArrayList();
        albumComboBox.setItems(albums);

        tags = FXCollections.observableArrayList();
        tagsListView.setItems(tags);
    }

    /**
     * Sets the current logged-in user and loads their albums into the combo box
     * @param user The current user to set
     */
    public void setUser(User user) {
        this.currentUser = user;
        albums.clear();
        albums.addAll(user.getAlbums());
    }

    /**
     * Sets the current album to display and refreshes the photo view
     * @param album The album to set as current
     */
    public void setAlbum(Album album) {
        this.currentAlbum = album;
        refreshPhotos();
    }

    /**
     * Sets the storage manager for data persistence
     * @param storageManager The storage manager instance
     */
    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * Refreshes the photo display in the tile pane
     * Configures layout settings and loads thumbnails for all photos in the current album
     */
    private void refreshPhotos() {
        photosTilePane.getChildren().clear();
        photosTilePane.setHgap(10); // Set horizontal gap between photos
        photosTilePane.setVgap(10); // Set vertical gap between photos
        photosTilePane.setPrefColumns(3); // Set number of columns
        photosTilePane.setPadding(new Insets(10));

        if (currentAlbum == null) return;

        List<Photo> photos = currentAlbum.getPhotos();
        for (Photo photo : photos) {
            ImageView imageView = createPhotoThumbnail(photo);
            System.out.println(imageView.getImage().getUrl());
            photosTilePane.getChildren().add(imageView);
        }

        // Reset selection state
        selectedPhoto = null;
        currentIndex = 0;
        clearPhotoDetails();
    }

    /**
     * Creates a thumbnail ImageView for a photo with click, hover, and selection effects
     * @param photo The photo to create thumbnail for
     * @return ImageView with thumbnail and interaction effects
     */
    private ImageView createPhotoThumbnail(Photo photo) {
        Image image = new Image(new File(photo.getPath()).toURI().toString(), 150, 150, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(350); // Force fixed width
        imageView.setFitHeight(350); // Force fixed height

        // Set click event to select photo
        imageView.setOnMouseClicked(event -> {
            selectedPhoto = photo;
            currentIndex = currentAlbum.getPhotos().indexOf(photo);
            displayPhotoDetails();
            System.out.println(selectedPhoto.getCaption());
        });

        // Set hover effects
        imageView.setStyle("-fx-border-color: transparent; -fx-padding: 5;");
        imageView.setOnMouseEntered(event -> {
            imageView.setStyle("-fx-border-color: #666; -fx-border-width: 2; -fx-padding: 3;");
        });
        imageView.setOnMouseExited(event -> {
            if (photo != selectedPhoto) {
                imageView.setStyle("-fx-border-color: transparent; -fx-padding: 5;");
            } else {
                imageView.setStyle("-fx-border-color: #007bff; -fx-border-width: 2; -fx-padding: 3;");
            }
        });

        return imageView;
    }

    /**
     * Displays details of the selected photo (caption, tags) and updates UI state
     */
    private void displayPhotoDetails() {
        if (selectedPhoto == null) return;

        // Display caption
        captionField.setText(selectedPhoto.getCaption());

        // Display tags
        tags.clear();
        for (com.example.photomanager.model.Tag tag : selectedPhoto.getTags()) {
            tags.add(tag.getName() + "=" + tag.getValue());
        }

        // Update thumbnail borders to indicate selection
        for (int i = 0; i < photosTilePane.getChildren().size(); i++) {
            ImageView imageView = (ImageView) photosTilePane.getChildren().get(i);
            if (i == currentIndex) {
                imageView.setStyle("-fx-border-color: #007bff; -fx-border-width: 2; -fx-padding: 3;");
            } else {
                imageView.setStyle("-fx-border-color: transparent; -fx-padding: 5;");
            }
        }

        // Update navigation button states
        prevButton.setDisable(currentIndex <= 0);
        nextButton.setDisable(currentIndex >= currentAlbum.getPhotos().size() - 1);
    }

    /**
     * Clears all photo detail fields in the UI
     */
    private void clearPhotoDetails() {
        captionField.clear();
        tags.clear();
        tagNameField.clear();
        tagValueField.clear();
    }

    /**
     * Handles the add photo button click event
     * Opens a file chooser to select photos and adds them to the current album
     * @param event The action event trigger
     */
    @FXML
    public void handleAddPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            int addedCount = 0;
            for (File file : selectedFiles) {
                Photo photo = new Photo(file.getAbsolutePath());
                if (currentAlbum.addPhoto(photo)) {
                    addedCount++;
                }
            }

            if (addedCount > 0) {
                storageManager.saveUsers();
                refreshPhotos();
                showAlert(Alert.AlertType.INFORMATION, "Add Successful", addedCount + " photos have been added");
            } else {
                showAlert(Alert.AlertType.WARNING, "Add Failed", "Selected photos already exist in the album");
            }
        }
    }

    /**
     * Handles the remove photo button click event
     * Shows confirmation dialog and removes the selected photo from the album if confirmed
     * @param event The action event trigger
     */
    @FXML
    public void handleRemovePhoto(ActionEvent event) {
        if (selectedPhoto == null) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Please select a photo to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this photo?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            currentAlbum.removePhoto(selectedPhoto);
            storageManager.saveUsers();
            refreshPhotos();
            showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Photo has been removed from the album");
        }
    }

    /**
     * Handles the save caption button click event
     * Saves the entered caption for the selected photo
     * @param event The action event trigger
     */
    @FXML
    public void handleSaveCaption(ActionEvent event) {
        if (selectedPhoto == null) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Please select a photo");
            return;
        }

        String caption = captionField.getText().trim();
        selectedPhoto.setCaption(caption);
        storageManager.saveUsers();
        showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Photo caption has been updated");
    }

    /**
     * Handles the previous photo button click event
     * Navigates to the previous photo in the album
     * @param event The action event trigger
     */
    @FXML
    public void handlePrev(ActionEvent event) {
        if (currentIndex > 0) {
            currentIndex--;
            selectedPhoto = currentAlbum.getPhotos().get(currentIndex);
            displayPhotoDetails();
        }
    }

    /**
     * Handles the next photo button click event
     * Navigates to the next photo in the album
     * @param event The action event trigger
     */
    @FXML
    public void handleNext(ActionEvent event) {
        if (currentIndex < currentAlbum.getPhotos().size() - 1) {
            currentIndex++;
            selectedPhoto = currentAlbum.getPhotos().get(currentIndex);
            displayPhotoDetails();
        }
    }

    /**
     * Handles the back button click event
     * Navigates back to the user's album list interface
     * @param event The action event trigger
     */
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("user.fxml"));
            Parent root = loader.load();

            UserController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setStorageManager(storageManager);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Photo Manager - " + currentUser.getUsername());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user interface");
        }
    }

    /**
     * Handles the copy button click event
     * Copies the selected photo to the target album (without removing from current album)
     * @param event The action event trigger
     */
    @FXML
    public void handleCopy(ActionEvent event) {
        if (selectedPhoto == null) {
            showAlert(Alert.AlertType.ERROR, "Copy Failed", "Please select a photo");
            return;
        }

        Album targetAlbum = albumComboBox.getValue();
        if (targetAlbum == null) {
            showAlert(Alert.AlertType.ERROR, "Copy Failed", "Please select a target album");
            return;
        }

        if (targetAlbum.equals(currentAlbum)) {
            showAlert(Alert.AlertType.ERROR, "Copy Failed", "Cannot copy to the current album");
            return;
        }

        if (targetAlbum.addPhoto(selectedPhoto)) {
            storageManager.saveUsers();
            showAlert(Alert.AlertType.INFORMATION, "Copy Successful", "Photo has been copied to album " + targetAlbum.getName());
        } else {
            showAlert(Alert.AlertType.WARNING, "Copy Failed", "The target album already contains this photo");
        }
    }

    /**
     * Handles the move button click event
     * Moves the selected photo from current album to target album
     * @param event The action event trigger
     */
    @FXML
    public void handleMove(ActionEvent event) {
        if (selectedPhoto == null) {
            showAlert(Alert.AlertType.ERROR, "Move Failed", "Please select a photo");
            return;
        }

        Album targetAlbum = albumComboBox.getValue();
        if (targetAlbum == null) {
            showAlert(Alert.AlertType.ERROR, "Move Failed", "Please select a target album");
            return;
        }

        if (targetAlbum.equals(currentAlbum)) {
            showAlert(Alert.AlertType.ERROR, "Move Failed", "Cannot move to the current album");
            return;
        }

        // First copy to target album
        boolean added = targetAlbum.addPhoto(selectedPhoto);
        if (added) {
            // Then remove from current album
            currentAlbum.removePhoto(selectedPhoto);
            storageManager.saveUsers();
            refreshPhotos();
            showAlert(Alert.AlertType.INFORMATION, "Move Successful", "Photo has been moved to album " + targetAlbum.getName());
        } else {
            showAlert(Alert.AlertType.WARNING, "Move Failed", "The target album already contains this photo");
        }
    }

    /**
     * Handles the add tag button click event
     * Adds a new tag to the selected photo (prevents duplicates)
     * @param event The action event trigger
     */
    @FXML
    public void handleAddTag(ActionEvent event) {
        if (selectedPhoto == null) {
            showAlert(Alert.AlertType.ERROR, "Add Failed", "Please select a photo");
            return;
        }

        String tagName = tagNameField.getText().trim();
        String tagValue = tagValueField.getText().trim();

        if (tagName.isEmpty() || tagValue.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Add Failed", "Tag name and value cannot be empty");
            return;
        }

        com.example.photomanager.model.Tag newTag = new com.example.photomanager.model.Tag(tagName, tagValue);
        if (selectedPhoto.addTag(newTag)) {
            tags.add(tagName + "=" + tagValue);
            tagNameField.clear();
            tagValueField.clear();
            storageManager.saveUsers();
            showAlert(Alert.AlertType.INFORMATION, "Add Successful", "Tag has been added");
        } else {
            showAlert(Alert.AlertType.WARNING, "Add Failed", "This tag already exists");
        }
    }

    /**
     * Handles the remove tag button click event
     * Removes the selected tag from the selected photo
     * @param event The action event trigger
     */
    @FXML
    public void handleRemoveTag(ActionEvent event) {
        if (selectedPhoto == null) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Please select a photo");
            return;
        }

        String selectedTagStr = tagsListView.getSelectionModel().getSelectedItem();
        if (selectedTagStr == null) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Please select a tag to delete");
            return;
        }

        // Parse tag name and value from string
        String[] parts = selectedTagStr.split("=", 2);
        if (parts.length != 2) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Invalid tag format");
            return;
        }

        com.example.photomanager.model.Tag tagToRemove = new com.example.photomanager.model.Tag(parts[0], parts[1]);
        if (selectedPhoto.removeTag(tagToRemove)) {
            tags.remove(selectedTagStr);
            storageManager.saveUsers();
            showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Tag has been deleted");
        }
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
