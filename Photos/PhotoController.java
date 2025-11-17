import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class PhotoController {

    @FXML private ImageView imageView;
    @FXML private Label captionLabel;
    @FXML private Label dateLabel;
    @FXML private ListView<String> tagListView;

    private Stage stage;
    private UserList users;
    private User user;
    private Album album;
    private Photo photo;

    public PhotoController(Stage stage, UserList users, User user, Album album, Photo photo) {
        this.stage = stage;
        this.users = users;
        this.user = user;
        this.album = album;
        this.photo = photo;
    }

    @FXML
    public void initialize() {
        loadPhoto();
    }

    /**
     * Loads and displays metadata
     */
    private void loadPhoto() {
        imageView.setImage(new Image(new File(photo.getFilePath()).toURI().toString()));
        captionLabel.setText(photo.getCaption());
        dateLabel.setText(photo.getDate().toString());
        refreshTagList();
    }

    private void refreshTagList() {
        tagListView.getItems().setAll(
                photo.getTags().stream().map(Tag::toString).toList()
        );
    }

    @FXML
    private void editCaption() {
        TextInputDialog dialog = new TextInputDialog(photo.getCaption());
        dialog.setHeaderText("Edit Caption");
        dialog.showAndWait().ifPresent(newCap -> {
            photo.setCaption(newCap);
            captionLabel.setText(newCap);
        });
    }

    @FXML
    private void addTag() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setHeaderText("Tag name:");
        var nameOpt = nameDialog.showAndWait();
        if (nameOpt.isEmpty()) return;

        TextInputDialog valueDialog = new TextInputDialog();
        valueDialog.setHeaderText("Tag value:");
        var valueOpt = valueDialog.showAndWait();
        if (valueOpt.isEmpty()) return;

        Tag t = new Tag(nameOpt.get(), valueOpt.get());
        photo.addTag(t);
        refreshTagList();
    }

    @FXML
    private void removeTag() {
        int i = tagListView.getSelectionModel().getSelectedIndex();
        if (i < 0) return;
        Tag selected = photo.getTags().get(i);
        photo.removeTag(selected);
        refreshTagList();
    }

    @FXML
    private void back() {
        try {
            SceneManager.switchScene(stage, "/view/Album.fxml",
                    new AlbumController(stage, users, user, album));
        } catch (Exception e) { e.printStackTrace(); }
    }
}
