import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class AlbumController {

    @FXML private ListView<String> photoListView;

    private Stage stage;
    private UserList users;
    private User user;
    private Album album;

    public AlbumController(Stage stage, UserList users, User user, Album album) {
        this.stage = stage;
        this.users = users;
        this.user = user;
        this.album = album;
    }

    @FXML
    public void initialize() {
        refreshPhotoList();
    }

    /**
     * Refreshes displayed photo file list
     */
    private void refreshPhotoList() {
        photoListView.setItems(FXCollections.observableArrayList(
                album.getPhotos().stream().map(Photo::getFilePath).toList()
        ));
    }

    /**
     * Add new photo using a FileChooser
     */
    @FXML
    private void addPhoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select a Photo");
        File file = fc.showOpenDialog(stage);
        if (file == null) return;

        // Create and add a Photo object
        Photo p = new Photo(file.getAbsolutePath());
        album.addPhoto(p);
        refreshPhotoList();
    }

    /**
     * Remove selected photo
     */
    @FXML
    private void removePhoto() {
        int index = photoListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showError("Select a photo to delete.");
            return;
        }
        Photo selected = album.getPhotos().get(index);
        album.removePhoto(selected);
        refreshPhotoList();
    }

    /**
     * Open selected photo in full detail view
     */
    @FXML
    private void openPhoto() {
        int index = photoListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showError("Select a photo to open.");
            return;
        }
        Photo selected = album.getPhotos().get(index);

        try {
            SceneManager.switchScene(stage, "/view/PhotoView.fxml",
                new PhotoController(stage, users, user, album, selected));
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Copy photo to another album (does not remove from this album)
     */
    @FXML
    private void copyPhoto() {
        handlePhotoTransfer(false); // false = copy
    }

    /**
     * Move photo to another album (does remove from this album)
     */
    @FXML
    private void movePhoto() {
        handlePhotoTransfer(true); // true = move
    }

    /**
     * Generic copy/move handler
     */
    private void handlePhotoTransfer(boolean move) {
        int index = photoListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showError("Select a photo first.");
            return;
        }
        Photo selected = album.getPhotos().get(index);

        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setHeaderText("Select target album:");
        List<String> albumNames = user.getAlbums().stream().map(Album::getName).toList();
        dialog.getItems().addAll(albumNames);
        dialog.showAndWait().ifPresent(targetName -> {
            Album target = user.getAlbums().stream()
                    .filter(a -> a.getName().equals(targetName)).findFirst().get();
            target.addPhoto(selected);
            if (move) album.removePhoto(selected);
            refreshPhotoList();
        });
    }

    /**
     * Return to user home
     */
    @FXML
    private void back() {
        try {
            SceneManager.switchScene(stage, "/view/UserHome.fxml",
                new UserHomeController(stage, users, user));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }
}
