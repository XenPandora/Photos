package com.example.photomanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Album class representing a user's photo album
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    /**
     * Constructor
     * @param name Name of the album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    /**
     * Add a photo to the album
     * @param photo The photo to add
     * @return true if the photo was added (no duplicate), false otherwise
     */
    public boolean addPhoto(Photo photo) {
        if (!photos.contains(photo)) {
            photos.add(photo);
            return true;
        }
        return false;
    }

    /**
     * Remove a photo from the album
     * @param photo The photo to remove
     * @return true if the photo was successfully removed, false otherwise
     */
    public boolean removePhoto(Photo photo) {
        return photos.remove(photo);
    }

    /**
     * Get the number of photos in the album
     * @return Total count of photos
     */
    public int getPhotoCount() {
        return photos.size();
    }

    /**
     * Get the earliest capture date of photos in the album
     * @return The earliest date, or null if the album is empty
     */
    public Date getEarliestDate() {
        if (photos.isEmpty()) return null;
        Date earliest = photos.get(0).getDateTaken();
        for (Photo photo : photos) {
            if (photo.getDateTaken().before(earliest)) {
                earliest = photo.getDateTaken();
            }
        }
        return earliest;
    }

    /**
     * Get the latest capture date of photos in the album
     * @return The latest date, or null if the album is empty
     */
    public Date getLatestDate() {
        if (photos.isEmpty()) return null;
        Date latest = photos.get(0).getDateTaken();
        for (Photo photo : photos) {
            if (photo.getDateTaken().after(latest)) {
                latest = photo.getDateTaken();
            }
        }
        return latest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return name.equals(album.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
