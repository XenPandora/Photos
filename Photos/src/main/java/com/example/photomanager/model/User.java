package com.example.photomanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User class representing a user in the system
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private boolean isAdmin;
    private List<Album> albums;

    /**
     * Constructor
     * @param username Username
     * @param password Password
     * @param isAdmin Whether the user is an administrator
     */
    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.albums = new ArrayList<>();

        // Create default album for regular users
        if (!isAdmin) {
            albums.add(new Album("Default Album"));
        }
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(albums);
    }

    /**
     * Create a new album
     * @param albumName Name of the album
     * @return The created album, or null if the name already exists
     */
    public Album createAlbum(String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return null; // Name already exists
            }
        }
        Album newAlbum = new Album(albumName);
        albums.add(newAlbum);
        return newAlbum;
    }

    /**
     * Delete an album
     * @param album The album to delete
     * @return Whether the deletion was successful
     */
    public boolean deleteAlbum(Album album) {
        return albums.remove(album);
    }

    /**
     * Find an album by name
     * @param albumName Name of the album to find
     * @return The found album, or null if not found
     */
    public Album findAlbum(String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }

    /**
     * Get all unique photos of the user
     * @return List of unique photos
     */
    public List<Photo> getAllPhotos() {
        List<Photo> allPhotos = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (!allPhotos.contains(photo)) {
                    allPhotos.add(photo);
                }
            }
        }
        return allPhotos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
