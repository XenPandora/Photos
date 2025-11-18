package com.example.photomanager.controller;

import com.example.photomanager.Application;
import com.example.photomanager.model.Album;
import com.example.photomanager.model.Photo;
import com.example.photomanager.model.StorageManager;
import com.example.photomanager.model.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Search Controller handling the logic for the search interface
 */
public class SearchController {
    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button dateSearchButton;

    @FXML
    private TextField tag1NameField;

    @FXML
    private TextField tag1ValueField;

    @FXML
    private TextField tag2NameField;

    @FXML
    private TextField tag2ValueField;

    @FXML
    private ComboBox<String> operatorComboBox;

    @FXML
    private Button tagSearchButton;

    @FXML
    private TilePane resultsTilePane;

    @FXML
    private Button backButton;

    @FXML
    private Button createAlbumButton;

    @FXML
    private TextField albumNameField;

    private User currentUser;
    private StorageManager storageManager;
    private List<Photo> searchResults;

    /**
     * Initialization method
     * Sets up the operator combo box and initializes search results list
     */
    @FXML
    public void initialize() {
        operatorComboBox.setItems(FXCollections.observableArrayList("AND", "OR"));
        operatorComboBox.setValue("AND");
        searchResults = new ArrayList<>();
    }

    /**
     * Sets the current logged-in user
     * @param user The current user to set
     */
    public void setUser(User user) {
        this.currentUser = user;
    }

    /**
     * Sets the storage manager for data persistence
     * @param storageManager The storage manager instance
     */
    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * Handles the date range search button click event
     * Filters photos based on the selected start and end dates
     * @param event The action event trigger
     */
    @FXML
    public void handleDateSearch(ActionEvent event) {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Search Failed", "Please select both start and end dates");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse(startDatePicker.getValue().toString());
            Date endDate = sdf.parse(endDatePicker.getValue().toString());

            // Include the last moment of the end date
            endDate.setTime(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);

            searchResults = new ArrayList<>();
            List<Photo> allPhotos = currentUser.getAllPhotos();

            for (Photo photo : allPhotos) {
                Date photoDate = photo.getDateTaken();
                if (photoDate.after(startDate) && photoDate.before(endDate)) {
                    searchResults.add(photo);
                }
            }

            displayResults();
            showAlert(Alert.AlertType.INFORMATION, "Search Completed", "Found " + searchResults.size() + " photos");
        } catch (ParseException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Search Failed", "Invalid date format");
        }
    }

    /**
     * Handles the tag search button click event
     * Filters photos based on single or double tag conditions (with AND/OR operator)
     * @param event The action event trigger
     */
    @FXML
    public void handleTagSearch(ActionEvent event) {
        String tag1Name = tag1NameField.getText().trim();
        String tag1Value = tag1ValueField.getText().trim();
        String tag2Name = tag2NameField.getText().trim();
        String tag2Value = tag2ValueField.getText().trim();
        String operator = operatorComboBox.getValue();

        if (tag1Name.isEmpty() || tag1Value.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Search Failed", "Please enter both name and value for the first tag");
            return;
        }

        searchResults = new ArrayList<>();
        List<Photo> allPhotos = currentUser.getAllPhotos();

        if (tag2Name.isEmpty() && tag2Value.isEmpty()) {
            // Single tag search
            for (Photo photo : allPhotos) {
                if (photo.hasTag(tag1Name, tag1Value)) {
                    searchResults.add(photo);
                }
            }
        } else {
            // Double tag search
            if (tag2Name.isEmpty() || tag2Value.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Search Failed", "Please enter both name and value for the second tag");
                return;
            }

            for (Photo photo : allPhotos) {
                boolean hasTag1 = photo.hasTag(tag1Name, tag1Value);
                boolean hasTag2 = photo.hasTag(tag2Name, tag2Value);

                if ("AND".equals(operator) && hasTag1 && hasTag2) {
                    searchResults.add(photo);
                } else if ("OR".equals(operator) && (hasTag1 || hasTag2)) {
                    searchResults.add(photo);
                }
            }
        }

        displayResults();
        showAlert(Alert.AlertType.INFORMATION, "Search Completed", "Found " + searchResults.size() + " photos");
    }

    /**
     * Displays the search results as thumbnails in the tile pane
     */
    private void displayResults() {
        resultsTilePane.getChildren().clear();

        for (Photo photo : searchResults) {
            ImageView imageView = createPhotoThumbnail(photo);
            resultsTilePane.getChildren().add(imageView);
        }
    }

    /**
     * Creates a thumbnail ImageView for a photo with click-to-view-details functionality
     * @param photo The photo to create thumbnail for
     * @return ImageView with thumbnail and click event listener
     */
    private ImageView createPhotoThumbnail(Photo photo) {
        Image image = new Image(new File(photo.getPath()).toURI().toString(), 150, 150, true, true);
        ImageView imageView = new ImageView(image);

        // Set click event to show photo details
        imageView.setOnMouseClicked(event -> {
            // Show photo details dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Photo Details");
            alert.setHeaderText(null);

            // Create large image display
            Image largeImage = new Image(new File(photo.getPath()).toURI().toString(), 400, 400, true, true);
            ImageView largeImageView = new ImageView(largeImage);

            // Build details text
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder details = new StringBuilder();
            details.append("Caption: ").append(photo.getCaption()).append("\n");
            details.append("Date Taken: ").append(sdf.format(photo.getDateTaken())).append("\n");
            details.append("Path: ").append(photo.getPath()).append("\n");
            details.append("Tags: ");

            List<com.example.photomanager.model.Tag> tags = photo.getTags();
            if (tags.isEmpty()) {
                details.append("None");
            } else {
                for (int i = 0; i < tags.size(); i++) {
                    com.example.photomanager.model.Tag tag = tags.get(i);
                    details.append(tag.getName()).append("=").append(tag.getValue());
                    if (i < tags.size() - 1) {
                        details.append(", ");
                    }
                }
            }

            // Set dialog content
            alert.getDialogPane().setGraphic(largeImageView);
            alert.getDialogPane().setContentText(details.toString());

            alert.showAndWait();
        });

        return imageView;
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
     * Handles the create album button click event
     * Creates a new album with the search results as its photos
     * @param event The action event trigger
     */
    @FXML
    public void handleCreateAlbum(ActionEvent event) {
        if (searchResults.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Creation Failed", "No search results to create album from");
            return;
        }

        String albumName = albumNameField.getText().trim();
        if (albumName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Creation Failed", "Please enter an album name");
            return;
        }

        if (currentUser.findAlbum(albumName) != null) {
            showAlert(Alert.AlertType.ERROR, "Creation Failed", "Album name already exists");
            return;
        }

        Album newAlbum = currentUser.createAlbum(albumName);
        for (Photo photo : searchResults) {
            newAlbum.addPhoto(photo);
        }

        storageManager.saveUsers();
        showAlert(Alert.AlertType.INFORMATION, "Creation Successful", "Album " + albumName + " created with " + searchResults.size() + " photos");
        albumNameField.clear();
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
