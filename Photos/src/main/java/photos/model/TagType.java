package photos.model;

import java.io.Serializable;

/**
 * Represents a tag type that defines how tags can be used.
 * Tag types can allow single or multiple values per photo.
 * 
 * @author Your Name
 */
public class TagType implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private boolean allowsMultipleValues;
    
    /**
     * Constructs a TagType with the specified name and value policy.
     * 
     * @param name the name of the tag type (e.g., "location", "person")
     * @param allowsMultipleValues true if multiple values are allowed for this tag type
     */
    public TagType(String name, boolean allowsMultipleValues) {
        this.name = name;
        this.allowsMultipleValues = allowsMultipleValues;
    }
    
    /**
     * Gets the name of the tag type.
     * 
     * @return the tag type name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the tag type.
     * 
     * @param name the tag type name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Checks if this tag type allows multiple values per photo.
     * 
     * @return true if multiple values are allowed
     */
    public boolean allowsMultipleValues() {
        return allowsMultipleValues;
    }
    
    /**
     * Sets whether this tag type allows multiple values.
     * 
     * @param allowsMultipleValues true if multiple values should be allowed
     */
    public void setAllowsMultipleValues(boolean allowsMultipleValues) {
        this.allowsMultipleValues = allowsMultipleValues;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TagType tagType = (TagType) obj;
        return name.equals(tagType.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
}

