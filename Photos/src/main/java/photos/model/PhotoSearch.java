package photos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for searching photos based on various criteria.
 * 
 * @author Your Name
 */
public class PhotoSearch {
    
    /**
     * Searches photos by date range.
     * 
     * @param photos the list of photos to search
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of photos within the date range
     */
    public static List<Photo> searchByDateRange(List<Photo> photos, LocalDateTime startDate, LocalDateTime endDate) {
        List<Photo> results = new ArrayList<>();
        for (Photo photo : photos) {
            LocalDateTime photoDate = photo.getDateTaken();
            if (photoDate != null) {
                if ((startDate == null || !photoDate.isBefore(startDate)) &&
                    (endDate == null || !photoDate.isAfter(endDate))) {
                    results.add(photo);
                }
            }
        }
        return results;
    }
    
    /**
     * Searches photos by a single tag-value pair.
     * 
     * @param photos the list of photos to search
     * @param tagName the tag name
     * @param tagValue the tag value
     * @return list of photos matching the tag
     */
    public static List<Photo> searchByTag(List<Photo> photos, String tagName, String tagValue) {
        List<Photo> results = new ArrayList<>();
        for (Photo photo : photos) {
            if (photo.hasTag(tagName, tagValue)) {
                results.add(photo);
            }
        }
        return results;
    }
    
    /**
     * Searches photos by conjunction (AND) of two tag-value pairs.
     * 
     * @param photos the list of photos to search
     * @param tagName1 the first tag name
     * @param tagValue1 the first tag value
     * @param tagName2 the second tag name
     * @param tagValue2 the second tag value
     * @return list of photos matching both tags
     */
    public static List<Photo> searchByTagAnd(List<Photo> photos, 
                                             String tagName1, String tagValue1,
                                             String tagName2, String tagValue2) {
        List<Photo> results = new ArrayList<>();
        for (Photo photo : photos) {
            if (photo.hasTag(tagName1, tagValue1) && photo.hasTag(tagName2, tagValue2)) {
                results.add(photo);
            }
        }
        return results;
    }
    
    /**
     * Searches photos by disjunction (OR) of two tag-value pairs.
     * 
     * @param photos the list of photos to search
     * @param tagName1 the first tag name
     * @param tagValue1 the first tag value
     * @param tagName2 the second tag name
     * @param tagValue2 the second tag value
     * @return list of photos matching either tag
     */
    public static List<Photo> searchByTagOr(List<Photo> photos,
                                            String tagName1, String tagValue1,
                                            String tagName2, String tagValue2) {
        List<Photo> results = new ArrayList<>();
        for (Photo photo : photos) {
            if (photo.hasTag(tagName1, tagValue1) || photo.hasTag(tagName2, tagValue2)) {
                results.add(photo);
            }
        }
        return results;
    }
}

