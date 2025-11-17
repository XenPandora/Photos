import java.io.*;

public class DataStore {

    private static final String DATA_FILE = "users.dat";

    public static void save(UserList list) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE));
        oos.writeObject(list);
        oos.close();
    }

    public static UserList load() throws Exception {
        File file = new File(DATA_FILE);
        if(!file.exists()) return new UserList(); // first run

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE));
        UserList list = (UserList) ois.readObject();
        ois.close();
        return list;
    }
}
