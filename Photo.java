import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class Photo implements Serializable {
    private String filePath; 
    private String caption;
    private LocalDateTime date;
    private ArrayList<Tag> tags = new ArrayList<>();

    public Photo(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        this.date = LocalDateTime.ofInstant(
                java.nio.file.Files.getLastModifiedTime(file.toPath()).toInstant(),
                ZoneId.systemDefault());
    }

    public String getFilePath() { return filePath; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public LocalDateTime getDate() { return date; }
    public ArrayList<Tag> getTags() { return tags; }

    public void addTag(Tag tag) {
        if(!tags.contains(tag))
            tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }
}
