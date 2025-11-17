package photos.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an album containing photos.
 * 
 * @author Your Name
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private List<Photo> photos;
    
    /**
     * Constructs an Album with the specified name.
     * 
     * @param name the name of the album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    /**
     * Gets the name of the album.
     * 
     * @return the album name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the album.
     * 
     * @param name the album name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the list of photos in this album.
     * 
     * @return the list of photos
     */
    public List<Photo> getPhotos() {
        return photos;
    }
    
    /**
     * Adds a photo to the album if it's not already present.
     * 
     * @param photo the photo to add
     */
    public void addPhoto(Photo photo) {
        if (photo != null && !photos.contains(photo)) {
            photos.add(photo);
        }
    }
    
    /**
     * Removes a photo from the album.
     * 
     * @param photo the photo to remove
     */
    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }
    
    /**
     * Gets the number of photos in this album.
     * 
     * @return the number of photos
     */
    public int getPhotoCount() {
        return photos.size();
    }
    
    /**
     * Gets the earliest date of photos in this album.
     * 
     * @return the earliest date, or null if album is empty
     */
    public LocalDateTime getEarliestDate() {
        if (photos.isEmpty()) {
            return null;
        }
        LocalDateTime earliest = photos.get(0).getDateTaken();
        for (Photo photo : photos) {
            if (photo.getDateTaken().isBefore(earliest)) {
                earliest = photo.getDateTaken();
            }
        }
        return earliest;
    }
    
    /**
     * Gets the latest date of photos in this album.
     * 
     * @return the latest date, or null if album is empty
     */
    public LocalDateTime getLatestDate() {
        if (photos.isEmpty()) {
            return null;
        }
        LocalDateTime latest = photos.get(0).getDateTaken();
        for (Photo photo : photos) {
            if (photo.getDateTaken().isAfter(latest)) {
                latest = photo.getDateTaken();
            }
        }
        return latest;
    }
    
    /**
     * Checks if the album contains a specific photo.
     * 
     * @param photo the photo to check
     * @return true if the album contains this photo
     */
    public boolean containsPhoto(Photo photo) {
        return photos.contains(photo);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
