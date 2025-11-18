package com.example.photomanager.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Photo class representing a single photo in the system
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String path;
    private String caption;
    private Date dateTaken;
    private List<Tag> tags;

    /**
     * Constructor
     * @param path File path of the photo
     */
    public Photo(String path) {
        this.path = path;
        this.caption = "";
        this.dateTaken = new Date(new File(path).lastModified());
        this.tags = new ArrayList<>();
    }

    // Getters and Setters
    public String getPath() {
        return path;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Add a tag to the photo
     * @param tag The tag to add
     * @return true if the tag was added (no duplicate), false otherwise
     */
    public boolean addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            return true;
        }
        return false;
    }

    /**
     * Remove a tag from the photo
     * @param tag The tag to remove
     * @return true if the tag was successfully removed, false otherwise
     */
    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
    }

    /**
     * Check if the photo contains a specific tag
     * @param tagName Name of the tag to check
     * @param tagValue Value of the tag to check
     * @return true if the photo has the specified tag, false otherwise
     */
    public boolean hasTag(String tagName, String tagValue) {
        return tags.contains(new Tag(tagName, tagValue));
    }

    /**
     * Get all tag values for a specific tag name
     * @param tagName Name of the tag to retrieve values for
     * @return List of tag values matching the specified tag name
     */
    public List<String> getTagValues(String tagName) {
        List<String> values = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getName().equals(tagName)) {
                values.add(tag.getValue());
            }
        }
        return values;
    }
}
