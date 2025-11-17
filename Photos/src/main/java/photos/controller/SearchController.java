package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.DataManager;
import photos.model.Photo;
import photos.model.PhotoSearch;
import photos.model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for the search screen.
 * Handles searching photos by date range or tags, and creating albums from results.
 * 
 * @author Your Name
 */
public class SearchController {
    @FXML
    private RadioButton dateRangeRadio;
    
    @FXML
    private RadioButton singleTagRadio;
    
    @FXML
    private RadioButton tagAndRadio;
    
    @FXML
    private RadioButton tagOrRadio;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private ComboBox<String> tagType1ComboBox;
    
    @FXML
    private TextField tagValue1Field;
    
    @FXML
    private ComboBox<String> tagType2ComboBox;
    
    @FXML
    private TextField tagValue2Field;
    
    @FXML
    private ListView<Photo> resultsListView;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button createAlbumButton;
    
    @FXML
    private Button backButton;
    
    private DataManager dataManager;
    private User currentUser;
    private List<Photo> searchResults;
    
    @FXML
    public void initialize() {
        dataManager = DataManager.getInstance();
        currentUser = dataManager.getCurrentUser();
        
        // Set up radio button group
        ToggleGroup searchTypeGroup = new ToggleGroup();
        dateRangeRadio.setToggleGroup(searchTypeGroup);
        singleTagRadio.setToggleGroup(searchTypeGroup);
        tagAndRadio.setToggleGroup(searchTypeGroup);
        tagOrRadio.setToggleGroup(searchTypeGroup);
        dateRangeRadio.setSelected(true);
        
        // Load tag types
        if (currentUser != null) {
            for (photos.model.TagType tagType : currentUser.getTagTypes()) {
                tagType1ComboBox.getItems().add(tagType.getName());
                tagType2ComboBox.getItems().add(tagType.getName());
            }
        }
        
        // Enable/disable fields based on search type
        updateSearchFields();
        
        searchTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateSearchFields();
        });
        
        resultsListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                createAlbumButton.setDisable(newValue == null || searchResults == null || searchResults.isEmpty());
            }
        );
    }
    
    private void updateSearchFields() {
        boolean dateRange = dateRangeRadio.isSelected();
        boolean singleTag = singleTagRadio.isSelected();
        boolean tagAnd = tagAndRadio.isSelected();
        boolean tagOr = tagOrRadio.isSelected();
        
        startDatePicker.setDisable(!dateRange);
        endDatePicker.setDisable(!dateRange);
        
        tagType1ComboBox.setDisable(!singleTag && !tagAnd && !tagOr);
        tagValue1Field.setDisable(!singleTag && !tagAnd && !tagOr);
        
        tagType2ComboBox.setDisable(!tagAnd && !tagOr);
        tagValue2Field.setDisable(!tagAnd && !tagOr);
    }
    
    @FXML
    private void handleSearch() {
        List<Photo> allPhotos = currentUser.getAllPhotos();
        List<Photo> results = null;
        
        if (dateRangeRadio.isSelected()) {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
            
            results = PhotoSearch.searchByDateRange(allPhotos, startDateTime, endDateTime);
        } else if (singleTagRadio.isSelected()) {
            String tagType1 = tagType1ComboBox.getSelectionModel().getSelectedItem();
            String tagValue1 = tagValue1Field.getText().trim();
            
            if (tagType1 == null || tagValue1.isEmpty()) {
                showAlert("Error", "Please select a tag type and enter a value");
                return;
            }
            
            results = PhotoSearch.searchByTag(allPhotos, tagType1, tagValue1);
        } else if (tagAndRadio.isSelected()) {
            String tagType1 = tagType1ComboBox.getSelectionModel().getSelectedItem();
            String tagValue1 = tagValue1Field.getText().trim();
            String tagType2 = tagType2ComboBox.getSelectionModel().getSelectedItem();
            String tagValue2 = tagValue2Field.getText().trim();
            
            if (tagType1 == null || tagValue1.isEmpty() || tagType2 == null || tagValue2.isEmpty()) {
                showAlert("Error", "Please select tag types and enter values for both tags");
                return;
            }
            
            results = PhotoSearch.searchByTagAnd(allPhotos, tagType1, tagValue1, tagType2, tagValue2);
        } else if (tagOrRadio.isSelected()) {
            String tagType1 = tagType1ComboBox.getSelectionModel().getSelectedItem();
            String tagValue1 = tagValue1Field.getText().trim();
            String tagType2 = tagType2ComboBox.getSelectionModel().getSelectedItem();
            String tagValue2 = tagValue2Field.getText().trim();
            
            if (tagType1 == null || tagValue1.isEmpty() || tagType2 == null || tagValue2.isEmpty()) {
                showAlert("Error", "Please select tag types and enter values for both tags");
                return;
            }
            
            results = PhotoSearch.searchByTagOr(allPhotos, tagType1, tagValue1, tagType2, tagValue2);
        }
        
        if (results != null) {
            searchResults = results;
            resultsListView.getItems().setAll(results);
            if (results.isEmpty()) {
                showAlert("No Results", "No photos found matching the search criteria");
            }
        }
    }
    
    @FXML
    private void handleCreateAlbum() {
        if (searchResults == null || searchResults.isEmpty()) {
            showAlert("Error", "No search results to create album from");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album from Search Results");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter album name:");
        
        dialog.showAndWait().ifPresent(albumName -> {
            if (albumName.trim().isEmpty()) {
                showAlert("Error", "Album name cannot be empty");
                return;
            }
            
            if (currentUser.getAlbumByName(albumName.trim()) != null) {
                showAlert("Error", "Album already exists");
                return;
            }
            
            Album newAlbum = new Album(albumName.trim());
            for (Photo photo : searchResults) {
                newAlbum.addPhoto(photo);
            }
            currentUser.addAlbum(newAlbum);
            dataManager.saveUsers();
            showAlert("Success", "Album created with " + searchResults.size() + " photos");
        });
    }
    
    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/fxml/user_dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setTitle("Photos Application - " + currentUser.getUsername());
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

