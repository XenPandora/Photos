package photos.model;

import java.io.Serializable;

/**
 * Represents a tag with a name-value pair for photos.
 * Tags are used to categorize and search photos.
 * 
 * @author Your Name
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String tagName;
    private String tagValue;
    
    /**
     * Constructs a Tag with the specified name and value.
     * 
     * @param tagName the name of the tag (e.g., "location", "person")
     * @param tagValue the value of the tag (e.g., "New Brunswick", "Alice")
     */
    public Tag(String tagName, String tagValue) {
        this.tagName = tagName;
        this.tagValue = tagValue;
    }
    
    /**
     * Gets the tag name.
     * 
     * @return the tag name
     */
    public String getTagName() {
        return tagName;
    }
    
    /**
     * Sets the tag name.
     * 
     * @param tagName the tag name to set
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    /**
     * Gets the tag value.
     * 
     * @return the tag value
     */
    public String getTagValue() {
        return tagValue;
    }
    
    /**
     * Sets the tag value.
     * 
     * @param tagValue the tag value to set
     */
    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return tagName.equals(tag.tagName) && tagValue.equals(tag.tagValue);
    }
    
    @Override
    public int hashCode() {
        return tagName.hashCode() * 31 + tagValue.hashCode();
    }
    
    @Override
    public String toString() {
        return tagName + "=" + tagValue;
    }
}

