package com.example.photomanager.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Storage Manager responsible for serializing and deserializing user data.
 */
public class StorageManager {
    private static final String STORAGE_DIR = System.getProperty("user.home") + File.separator + ".photoManager";
    private static final String USERS_FILE = STORAGE_DIR + File.separator + "users.dat";

    private List<User> users;

    /**
     * Constructor that loads user data and initializes the storage.
     */
    public StorageManager() {
        // Create storage directory if it doesn't exist
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Load user data from file
        users = loadUsers();

        // Check and create default admin user if not exists
        if (findUser("admin") == null) {
            User admin = new User("admin", "admin", true);
            users.add(admin);
            saveUsers();
        }

        // Check and create default stock user if not exists
        if (findUser("stock") == null) {
            User stockUser = new User("stock", "stock", false);
            Album stockAlbum = stockUser.createAlbum("stock");

            // Add stock photos to the stock album
            addStockPhotos(stockAlbum);

            users.add(stockUser);
            saveUsers();
        }
    }

    /**
     * Adds stock photos to the specified album.
     * @param stockAlbum The album to which stock photos will be added
     */
    private void addStockPhotos(Album stockAlbum) {
        // Load stock photos from resources directory
        try {
            // Assuming stock photos are located in the project's resources/stock_photos directory
            // During runtime, these photos need to be copied to the user's storage directory
            File stockDir = new File("src/main/resources/stock_photos");
            if (stockDir.exists() && stockDir.isDirectory()) {
                File[] photoFiles = stockDir.listFiles((dir, name) -> {
                    String lower = name.toLowerCase();
                    return lower.endsWith(".jpg") || lower.endsWith(".png") || lower.endsWith(".jpeg");
                });

                if (photoFiles != null) {
                    for (File file : photoFiles) {
                        // Copy file to storage directory
                        File destFile = new File(STORAGE_DIR + File.separator + "stock_photos" + File.separator + file.getName());
                        destFile.getParentFile().mkdirs();

                        // Copy file content
                        try (FileInputStream fis = new FileInputStream(file);
                             FileOutputStream fos = new FileOutputStream(destFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        }

                        // Add photo to album
                        Photo photo = new Photo(destFile.getAbsolutePath());
                        stockAlbum.addPhoto(photo);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads user data from the serialized file.
     * @return List of User objects loaded from file, or empty list if file doesn't exist or error occurs
     */
    private List<User> loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Saves user data to the serialized file.
     */
    public void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds a user by username.
     * @param username The username to search for
     * @return The User object if found, null otherwise
     */
    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Creates a new user with default (non-admin) privileges.
     * @param username The username for the new user
     * @param password The password for the new user
     * @return The created User object, or null if username already exists
     */
    public User createUser(String username, String password) {
        if (findUser(username) != null) {
            return null;
        }

        User newUser = new User(username, password, false);
        users.add(newUser);
        saveUsers();
        return newUser;
    }

    /**
     * Deletes a user by username.
     * @param username The username of the user to delete
     * @return true if user was found and deleted, false otherwise
     */
    public boolean deleteUser(String username) {
        User user = findUser(username);
        if (user != null) {
            users.remove(user);
            saveUsers();
            return true;
        }
        return false;
    }

    /**
     * Gets all users in the system.
     * @return A new ArrayList containing all User objects
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
