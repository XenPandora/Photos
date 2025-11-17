import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    private String name;
    private ArrayList<Photo> photos = new ArrayList<>();

    public Album(String name) { this.name = name; }

    public String getName() { return name; }
    public ArrayList<Photo> getPhotos() { return photos; }

    public void rename(String newName) { this.name = newName; }

    public void addPhoto(Photo p) {
        if(!photos.contains(p))
            photos.add(p);
    }

    public void removePhoto(Photo p) {
        photos.remove(p);
    }
}