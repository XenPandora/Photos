import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    public static void switchScene(Stage stage, String fxml, Object controller) throws Exception {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
        loader.setController(controller);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
