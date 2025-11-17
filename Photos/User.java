import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private ArrayList<Album> albums = new ArrayList<>();

    public User(String username) { this.username = username; }
    public String getUsername() { return username; }
    public ArrayList<Album> getAlbums() { return albums; }

    public void addAlbum(Album a) { albums.add(a); }
    public void deleteAlbum(Album a) { albums.remove(a); }
}
