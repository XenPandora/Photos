import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SearchController {

    @FXML private ListView<String> resultsList;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField tagName1;
    @FXML private TextField tagVal1;
    @FXML private TextField tagName2;
    @FXML private TextField tagVal2;
    @FXML private ChoiceBox<String> andOrChoice;

    private Stage stage;
    private User user;
    private UserList users;
    private List<Photo> results = new ArrayList<>();

    public SearchController(Stage stage, UserList users, User user) {
        this.stage = stage;
        this.user = user;
        this.users = users;
    }

    @FXML
    public void initialize() {
        andOrChoice.getItems().addAll("AND", "OR");
    }

    @FXML
    private void searchByDate() {
        results.clear();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        if (start == null || end == null) return;

        for (Album a : user.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                LocalDate d = p.getDate().toLocalDate();
                if (!d.isBefore(start) && !d.isAfter(end)) {
                    if (!results.contains(p)) results.add(p);
                }
            }
        }
        showResults();
    }

    @FXML
    private void searchByTag() {
        results.clear();
        String n1 = tagName1.getText().trim();
        String v1 = tagVal1.getText().trim();
        if (n1.isEmpty() || v1.isEmpty()) return;

        Tag t1 = new Tag(n1, v1);

        for (Album a : user.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                if (p.getTags().contains(t1) && !results.contains(p)) {
                    results.add(p);
                }
            }
        }
        showResults();
    }

    @FXML
    private void searchByTagCombo() {
        results.clear();
        String n1 = tagName1.getText().trim();
        String v1 = tagVal1.getText().trim();
        String n2 = tagName2.getText().trim();
        String v2 = tagVal2.getText().trim();
        String mode = andOrChoice.getValue();

        if (n1.isEmpty() || v1.isEmpty() || n2.isEmpty() || v2.isEmpty() || mode == null) return;

        Tag t1 = new Tag(n1, v1);
        Tag t2 = new Tag(n2, v2);

        for (Album a : user.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                boolean has1 = p.getTags().contains(t1);
                boolean has2 = p.getTags().contains(t2);

                if (mode.equals("AND") && has1 && has2 && !results.contains(p))
                    results.add(p);
                if (mode.equals("OR") && (has1 || has2) && !results.contains(p))
                    results.add(p);
            }
        }
        showResults();
    }

    private void showResults() {
        resultsList.setItems(FXCollections.observableArrayList(
                results.stream().map(Photo::getFilePath).toList()
        ));
    }

    @FXML
    private void makeAlbum() {
        if (results.isEmpty()) return;

        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Enter new album name:");
        d.showAndWait().ifPresent(name -> {
            Album newAlbum = new Album(name);
            for (Photo p : results) newAlbum.addPhoto(p);
            user.addAlbum(newAlbum);
        });
    }

    @FXML
    private void back() {
        try {
            SceneManager.switchScene(stage, "/view/UserHome.fxml",
                    new UserHomeController(stage, users, user));
        } catch (Exception e) { e.printStackTrace(); }
    }
}
