import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    private Stage stage;
    private UserList users;

    public LoginController(Stage stage, UserList users) {
        this.stage = stage;
        this.users = users;
    }

    @FXML
    private void login() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }

        if (username.equals("admin")) {
            try {
                SceneManager.switchScene(stage, "/view/Admin.fxml",
                        new AdminController(stage, users));
            } catch (Exception e) { e.printStackTrace(); }
            return;
        }

        User user = users.findUser(username);
        if (user == null) {
            showError("User does not exist.");
            return;
        }

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
