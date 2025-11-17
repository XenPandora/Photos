package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user with albums and tag types.
 * 
 * @author Your Name
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private List<Album> albums;
    private List<TagType> tagTypes;
    
    /**
     * Constructs a User with the specified username.
     * Initializes default tag types (location and person).
     * 
     * @param username the username
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
        this.tagTypes = new ArrayList<>();
        // Initialize default tag types
        tagTypes.add(new TagType("location", false)); // single value
        tagTypes.add(new TagType("person", true)); // multiple values
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the list of albums for this user.
     * 
     * @return the list of albums
     */
    public List<Album> getAlbums() {
        return albums;
    }
    
    /**
     * Adds an album to the user's collection.
     * 
     * @param album the album to add
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }
    
    /**
     * Removes an album from the user's collection.
     * 
     * @param album the album to remove
     */
    public void removeAlbum(Album album) {
        albums.remove(album);
    }
    
    /**
     * Gets an album by name.
     * 
     * @param name the album name
     * @return the album with the given name, or null if not found
     */
    public Album getAlbumByName(String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }
        return null;
    }
    
    /**
     * Gets the list of tag types for this user.
     * 
     * @return the list of tag types
     */
    public List<TagType> getTagTypes() {
        return tagTypes;
    }
    
    /**
     * Adds a tag type to the user's collection.
     * 
     * @param tagType the tag type to add
     */
    public void addTagType(TagType tagType) {
        if (tagType != null && !tagTypes.contains(tagType)) {
            tagTypes.add(tagType);
        }
    }
    
    /**
     * Gets a tag type by name.
     * 
     * @param name the tag type name
     * @return the tag type with the given name, or null if not found
     */
    public TagType getTagTypeByName(String name) {
        for (TagType tagType : tagTypes) {
            if (tagType.getName().equals(name)) {
                return tagType;
            }
        }
        return null;
    }
    
    /**
     * Gets all photos across all albums for this user.
     * 
     * @return list of all photos
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
    public String toString() {
        return username;
    }
}
