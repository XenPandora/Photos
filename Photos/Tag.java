import java.io.Serializable;

public class Tag implements Serializable {
    private String name;
    private String value;

    public Tag(String name, String value) {
        this.name = name.toLowerCase();
        this.value = value.toLowerCase();
    }

    public String getName() { return name; }
    public String getValue() { return value; }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Tag)) return false;
        Tag t = (Tag) obj;
        return t.name.equals(this.name) && t.value.equals(this.value);
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
