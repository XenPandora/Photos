package photos.model;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a photo with file path, caption, date, and tags.
 * The date is based on the file's last modification time.
 * 
 * @author Your Name
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    private String caption;
    private LocalDateTime dateTaken;
    private List<Tag> tags;
    
    /**
     * Constructs a Photo with the specified file path.
     * The date is automatically set from the file's last modification time.
     * 
     * @param filePath the path to the photo file
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        this.caption = "";
        this.tags = new ArrayList<>();
        updateDateFromFile();
    }
    
    /**
     * Updates the date from the file's last modification time.
     */
    public void updateDateFromFile() {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                long lastModified = file.lastModified();
                this.dateTaken = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(lastModified),
                    ZoneId.systemDefault()
                );
            } else {
                this.dateTaken = LocalDateTime.now();
            }
        } catch (Exception e) {
            this.dateTaken = LocalDateTime.now();
        }
    }
    
    /**
     * Gets the file path of the photo.
     * 
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Sets the file path of the photo.
     * 
     * @param filePath the file path to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
        updateDateFromFile();
    }
    
    /**
     * Gets the caption of the photo.
     * 
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }
    
    /**
     * Sets the caption of the photo.
     * 
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    /**
     * Gets the date the photo was taken (file modification date).
     * 
     * @return the date taken
     */
    public LocalDateTime getDateTaken() {
        return dateTaken;
    }
    
    /**
     * Gets the list of tags for this photo.
     * 
     * @return the list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }
    
    /**
     * Adds a tag to the photo if it doesn't already exist.
     * 
     * @param tag the tag to add
     */
    public void addTag(Tag tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    /**
     * Removes a tag from the photo.
     * 
     * @param tag the tag to remove
     */
    public void removeTag(Tag tag) {
        tags.remove(tag);
    }
    
    /**
     * Checks if the photo has a specific tag.
     * 
     * @param tagName the tag name
     * @param tagValue the tag value
     * @return true if the photo has this tag
     */
    public boolean hasTag(String tagName, String tagValue) {
        Tag tag = new Tag(tagName, tagValue);
        return tags.contains(tag);
    }
    
    /**
     * Gets all tag values for a specific tag name.
     * 
     * @param tagName the tag name
     * @return list of tag values for this tag name
     */
    public List<String> getTagValues(String tagName) {
        List<String> values = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getTagName().equals(tagName)) {
                values.add(tag.getTagValue());
            }
        }
        return values;
    }
    
    @Override
    public String toString() {
        return caption.isEmpty() ? filePath : caption;
    }
}
