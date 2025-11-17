package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.DataManager;
import photos.model.Photo;
import photos.model.Tag;
import photos.model.TagType;
import photos.model.User;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the album view screen.
 * Handles displaying photos, managing tags, slideshow, and copy/move operations.
 * 
 * @author Your Name
 */
public class AlbumViewController {
    @FXML
    private ListView<Photo> photoListView;
    
    @FXML
    private ImageView photoImageView;
    
    @FXML
    private Label captionLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private TextField captionField;
    
    @FXML
    private ComboBox<String> tagTypeComboBox;
    
    @FXML
    private TextField tagValueField;
    
    @FXML
    private ListView<String> tagsListView;
    
    @FXML
    private Button addPhotoButton;
    
    @FXML
    private Button deletePhotoButton;
    
    @FXML
    private Button addTagButton;
    
    @FXML
    private Button deleteTagButton;
    
    @FXML
    private Button saveCaptionButton;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private Button copyButton;
    
    @FXML
    private Button moveButton;
    
    @FXML
    private Button backButton;
    
    private Album album;
    private DataManager dataManager;
    private User currentUser;
    private int currentPhotoIndex;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        currentUser = dataManager.getCurrentUser();
        
        // Load tag types into combo box
        if (currentUser != null) {
            for (TagType tagType : currentUser.getTagTypes()) {
                tagTypeComboBox.getItems().add(tagType.getName());
            }
        }
        
        // Enable/disable buttons based on selection
        photoListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    currentPhotoIndex = photoListView.getItems().indexOf(newValue);
                    displayPhoto(newValue);
                    deletePhotoButton.setDisable(false);
                    updateNavigationButtons();
                } else {
                    photoImageView.setImage(null);
                    captionField.clear();
                    captionLabel.setText("");
                    dateLabel.setText("");
                    tagsListView.getItems().clear();
                    deletePhotoButton.setDisable(true);
                    prevButton.setDisable(true);
                    nextButton.setDisable(true);
                    copyButton.setDisable(true);
                    moveButton.setDisable(true);
                }
            }
        );
        
        tagsListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                deleteTagButton.setDisable(newValue == null);
            }
        );
    }
    
    /**
     * Sets the album to display.
     * 
     * @param album the album to set
     */
    public void setAlbum(Album album) {
        this.album = album;
        if (album != null) {
            photoListView.getItems().setAll(album.getPhotos());
            if (!album.getPhotos().isEmpty()) {
                photoListView.getSelectionModel().select(0);
            }
        }
    }
    
    /**
     * Displays a photo with its details.
     * 
     * @param photo the photo to display
     */
    private void displayPhoto(Photo photo) {
        try {
            File file = new File(photo.getFilePath());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                photoImageView.setImage(image);
                photoImageView.setPreserveRatio(true);
            } else {
                photoImageView.setImage(null);
            }
        } catch (Exception e) {
            photoImageView.setImage(null);
        }
        
        captionField.setText(photo.getCaption());
        captionLabel.setText("Caption: " + (photo.getCaption().isEmpty() ? "(none)" : photo.getCaption()));
        dateLabel.setText("Date: " + photo.getDateTaken().format(DATE_FORMATTER));
        
        // Display tags
        List<String> tagStrings = new java.util.ArrayList<>();
        for (Tag tag : photo.getTags()) {
            tagStrings.add(tag.toString());
        }
        tagsListView.getItems().setAll(tagStrings);
    }
    
    /**
     * Updates navigation buttons based on current position.
     */
    private void updateNavigationButtons() {
        int size = photoListView.getItems().size();
        prevButton.setDisable(currentPhotoIndex <= 0);
        nextButton.setDisable(currentPhotoIndex >= size - 1);
        copyButton.setDisable(size == 0);
        moveButton.setDisable(size == 0);
    }
    
    @FXML
    private void handleAddPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp")
        );
        
        File selectedFile = fileChooser.showOpenDialog(addPhotoButton.getScene().getWindow());
        if (selectedFile != null) {
            // Check if photo already exists in this album
            Photo newPhoto = new Photo(selectedFile.getPath());
            if (album.containsPhoto(newPhoto)) {
                showAlert("Error", "This photo is already in the album");
                return;
            }
            
            album.addPhoto(newPhoto);
            dataManager.saveUsers();
            photoListView.getItems().setAll(album.getPhotos());
            photoListView.getSelectionModel().select(newPhoto);
        }
    }
    
    @FXML
    private void handleDeletePhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Photo");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this photo from the album?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            album.removePhoto(selectedPhoto);
            dataManager.saveUsers();
            photoListView.getItems().setAll(album.getPhotos());
            if (!album.getPhotos().isEmpty()) {
                int newIndex = Math.min(currentPhotoIndex, album.getPhotos().size() - 1);
                photoListView.getSelectionModel().select(newIndex);
            }
        }
    }
    
    @FXML
    private void handleSaveCaption() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            selectedPhoto.setCaption(captionField.getText());
            dataManager.saveUsers();
            displayPhoto(selectedPhoto);
            photoListView.refresh();
        }
    }
    
    @FXML
    private void handleAddTag() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            showAlert("Error", "Please select a photo");
            return;
        }
        
        String tagTypeName = tagTypeComboBox.getSelectionModel().getSelectedItem();
        String tagValue = tagValueField.getText().trim();
        
        if (tagTypeName == null || tagTypeName.isEmpty()) {
            showAlert("Error", "Please select a tag type");
            return;
        }
        
        if (tagValue.isEmpty()) {
            showAlert("Error", "Please enter a tag value");
            return;
        }
        
        TagType tagType = currentUser.getTagTypeByName(tagTypeName);
        if (tagType == null) {
            showAlert("Error", "Tag type not found");
            return;
        }
        
        // Check if tag type allows multiple values
        if (!tagType.allowsMultipleValues()) {
            // Check if photo already has a value for this tag type
            List<String> existingValues = selectedPhoto.getTagValues(tagTypeName);
            if (!existingValues.isEmpty()) {
                showAlert("Error", "This tag type only allows one value. Please delete the existing tag first.");
                return;
            }
        }
        
        Tag newTag = new Tag(tagTypeName, tagValue);
        selectedPhoto.addTag(newTag);
        dataManager.saveUsers();
        displayPhoto(selectedPhoto);
        tagValueField.clear();
    }
    
    @FXML
    private void handleDeleteTag() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        String selectedTagString = tagsListView.getSelectionModel().getSelectedItem();
        
        if (selectedPhoto != null && selectedTagString != null) {
            // Parse tag string (format: "name=value")
            String[] parts = selectedTagString.split("=", 2);
            if (parts.length == 2) {
                Tag tagToRemove = new Tag(parts[0], parts[1]);
                selectedPhoto.removeTag(tagToRemove);
                dataManager.saveUsers();
                displayPhoto(selectedPhoto);
            }
        }
    }
    
    @FXML
    private void handlePrev() {
        if (currentPhotoIndex > 0) {
            currentPhotoIndex--;
            photoListView.getSelectionModel().select(currentPhotoIndex);
        }
    }
    
    @FXML
    private void handleNext() {
        if (currentPhotoIndex < photoListView.getItems().size() - 1) {
            currentPhotoIndex++;
            photoListView.getSelectionModel().select(currentPhotoIndex);
        }
    }
    
    @FXML
    private void handleCopy() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            return;
        }
        
        // Show dialog to select destination album
        List<Album> albums = currentUser.getAlbums();
        if (albums.size() <= 1) {
            showAlert("Error", "No other albums available to copy to");
            return;
        }
        
        ChoiceDialog<Album> dialog = new ChoiceDialog<>(null, albums);
        dialog.setTitle("Copy Photo");
        dialog.setHeaderText(null);
        dialog.setContentText("Select destination album:");
        
        dialog.showAndWait().ifPresent(destAlbum -> {
            if (destAlbum != album && !destAlbum.containsPhoto(selectedPhoto)) {
                destAlbum.addPhoto(selectedPhoto);
                dataManager.saveUsers();
                showAlert("Success", "Photo copied to " + destAlbum.getName());
            } else if (destAlbum == album) {
                showAlert("Error", "Photo is already in this album");
            } else {
                showAlert("Error", "Photo already exists in destination album");
            }
        });
    }
    
    @FXML
    private void handleMove() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            return;
        }
        
        // Check if photo is in multiple albums
        int albumCount = 0;
        for (Album a : currentUser.getAlbums()) {
            if (a.containsPhoto(selectedPhoto)) {
                albumCount++;
            }
        }
        
        if (albumCount <= 1) {
            showAlert("Error", "Photo must be in at least one album. Use copy instead.");
            return;
        }
        
        // Show dialog to select destination album
        List<Album> albums = currentUser.getAlbums();
        ChoiceDialog<Album> dialog = new ChoiceDialog<>(null, albums);
        dialog.setTitle("Move Photo");
        dialog.setHeaderText(null);
        dialog.setContentText("Select destination album:");
        
        dialog.showAndWait().ifPresent(destAlbum -> {
            if (destAlbum != album && !destAlbum.containsPhoto(selectedPhoto)) {
                album.removePhoto(selectedPhoto);
                destAlbum.addPhoto(selectedPhoto);
                dataManager.saveUsers();
                photoListView.getItems().setAll(album.getPhotos());
                if (!album.getPhotos().isEmpty()) {
                    int newIndex = Math.min(currentPhotoIndex, album.getPhotos().size() - 1);
                    photoListView.getSelectionModel().select(newIndex);
                }
                showAlert("Success", "Photo moved to " + destAlbum.getName());
            } else if (destAlbum == album) {
                showAlert("Error", "Photo is already in this album");
            } else {
                showAlert("Error", "Photo already exists in destination album");
            }
        });
    }
    
    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/fxml/user_dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setTitle("Photos Application - " + dataManager.getCurrentUser().getUsername());
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Error", "Failed to go back: " + e.getMessage());
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
