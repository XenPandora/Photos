import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminController {

    @FXML private ListView<String> userListView;
    private UserList users;
    private Stage stage;

    public AdminController(Stage stage, UserList users) {
        this.stage = stage;
        this.users = users;
    }

    @FXML
    public void initialize() {
        refreshUserList();
    }

    private void refreshUserList() {
        userListView.setItems(FXCollections.observableArrayList(
                users.getUsers().stream().map(User::getUsername).toList()
        ));
    }

    @FXML
    private void createUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter new username:");
        dialog.showAndWait().ifPresent(name -> {
            if (users.findUser(name) != null) {
                showError("User already exists.");
                return;
            }
            users.addUser(new User(name));
            refreshUserList();
        });
    }

    @FXML
    private void deleteUser() {
        String selected = userListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a user to delete.");
            return;
        }
        User user = users.findUser(selected);
        users.removeUser(user);
        refreshUserList();
    }

    @FXML
    private void logout() {
        try {
            DataStore.save(users);
            SceneManager.switchScene(stage, "/view/Login.fxml",
                    new LoginController(stage, users));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }
}

