package photos.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages user data persistence and provides singleton access to user data.
 * Handles saving and loading user data from disk using serialization.
 * 
 * @author Your Name
 */
public class DataManager {
    private static final String DATA_FILE = "data/users.dat";
    private List<User> users;
    private User currentUser;
    
    private static DataManager instance;
    
    /**
     * Private constructor for singleton pattern.
     * Initializes users and creates default admin and stock users if they don't exist.
     */
    private DataManager() {
        users = new ArrayList<>();
        loadUsers();
        // Create admin user if it doesn't exist
        if (getUser("admin") == null) {
            users.add(new User("admin"));
            saveUsers();
        }
        // Create stock user for demo
        if (getUser("stock") == null) {
            User stockUser = new User("stock");
            Album album = new Album("stock");
            // Add stock photos
            for (int i = 1; i <= 5; i++) {
                Photo photo = new Photo("data/stock" + i + ".jpg");
                photo.setCaption("Stock Photo " + i);
                album.addPhoto(photo);
            }
            stockUser.addAlbum(album);
            users.add(stockUser);
            saveUsers();
        }
    }
    
    /**
     * Gets the singleton instance of DataManager.
     * 
     * @return the DataManager instance
     */
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    /**
     * Gets the list of all users.
     * 
     * @return the list of users
     */
    public List<User> getUsers() {
        return users;
    }
    
    /**
     * Gets the currently logged in user.
     * 
     * @return the current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Sets the currently logged in user.
     * 
     * @param user the user to set as current
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Gets a user by username.
     * 
     * @param username the username to search for
     * @return the user with the given username, or null if not found
     */
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Adds a new user to the system and saves to disk.
     * 
     * @param user the user to add
     */
    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }
    
    /**
     * Saves all user data to disk using serialization.
     * Errors are handled silently to avoid console output.
     */
    public void saveUsers() {
        try {
            // Ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(DATA_FILE))) {
                oos.writeObject(users);
            }
        } catch (IOException e) {
            // Error handled silently - no console output per requirements
        }
    }
    
    /**
     * Loads user data from disk using deserialization.
     * Errors are handled silently to avoid console output.
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(DATA_FILE))) {
                users = (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                // Error handled silently - no console output per requirements
                users = new ArrayList<>();
            }
        }
    }
}
