import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UserHomeController {

    @FXML private ListView<String> albumListView;

    private User user;
    private UserList users;
    private Stage stage;

    public UserHomeController(Stage stage, UserList users, User user) {
        this.stage = stage;
        this.users = users;
        this.user = user;
    }

    @FXML
    public void initialize() {
        refreshAlbumList();
    }

    private void refreshAlbumList() {
        albumListView.setItems(FXCollections.observableArrayList(
                user.getAlbums().stream().map(Album::getName).toList()
        ));
    }

    @FXML
    private void createAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("New Album Name:");
        dialog.showAndWait().ifPresent(name -> {
            user.addAlbum(new Album(name));
            refreshAlbumList();
        });
    }

    @FXML
    private void deleteAlbum() {
        int index = albumListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;
        user.getAlbums().remove(index);
        refreshAlbumList();
    }

    @FXML
    private void openAlbum() {
        int index = albumListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;
        Album selectedAlbum = user.getAlbums().get(index);

        try {
            SceneManager.switchScene(stage, "/view/Album.fxml",
                    new AlbumController(stage, users, user, selectedAlbum));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void logout() {
        try {
            DataStore.save(users);
            SceneManager.switchScene(stage, "/view/Login.fxml",
                    new LoginController(stage, users));
        } catch (Exception e) { e.printStackTrace(); }
    }
}
