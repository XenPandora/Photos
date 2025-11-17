package photos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.DataManager;

import java.io.IOException;

/**
 * Main application class for the Photos application.
 * Entry point for the JavaFX photo management application.
 * 
 * @author Your Name
 */
public class Photos extends Application {
    
    /**
     * Starts the JavaFX application.
     * Initializes the data manager and displays the login screen.
     * 
     * @param stage the primary stage for this application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize data manager
        DataManager.getInstance();
        
        // Load login scene
        FXMLLoader fxmlLoader = new FXMLLoader(Photos.class.getResource("/photos/view/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Photos Application - Login");
        stage.setScene(scene);
        
        // Handle window close event to save data
        stage.setOnCloseRequest(event -> {
            DataManager.getInstance().saveUsers();
        });
        
        stage.show();
    }
    
    /**
     * Stops the application and saves all data.
     */
    @Override
    public void stop() {
        DataManager.getInstance().saveUsers();
    }

    /**
     * Main method to launch the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}

