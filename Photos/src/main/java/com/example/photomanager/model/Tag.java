package com.example.photomanager.model;

import java.io.Serializable;

/**
 * Tag class representing metadata information for a photo.
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    /**
     * Constructor
     * @param name  The name of the tag (e.g., "Location", "Person")
     * @param value The value of the tag (e.g., "Paris", "John Doe")
     */
    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Compares this tag to the specified object.
     * The result is true if and only if the argument is not null and is a Tag object that contains the same name and value as this object.
     *
     * @param o The object to compare this Tag against.
     * @return true if the given object represents a Tag equivalent to this tag, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name) && value.equals(tag.value);
    }

    /**
     * Returns a hash code value for the tag.
     * This method is supported for the benefit of hash tables such as those provided by java.util.HashMap.
     *
     * @return a hash code value for this tag.
     */
    @Override
    public int hashCode() {
        return name.hashCode() + value.hashCode();
    }

    /**
     * Returns a string representation of the tag.
     * The string representation is of the form "name=value".
     *
     * @return a string representation of the tag.
     */
    @Override
    public String toString() {
        return name + "=" + value;
    }
}
