import java.io.Serializable;
import java.util.ArrayList;

public class UserList implements Serializable {
    private ArrayList<User> users = new ArrayList<>();

    public ArrayList<User> getUsers() { return users; }

    public User findUser(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    public void addUser(User u) { users.add(u); }
    public void removeUser(User u) { users.remove(u); }
}
