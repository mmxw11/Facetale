package wepa.ftale.web.profile;

/**
 * @author Matias
 */
public enum ProfileViewDisplayType {
    
    POSTS,
    ALBUM;

    public static ProfileViewDisplayType parse(String name) {
        for (ProfileViewDisplayType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}