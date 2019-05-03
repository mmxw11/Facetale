package wepa.ftale.web.profile;

/**
 * This enum represents user's relationship to another user.
 * 
 * @author Matias
 */
public enum UserRelationship {
    
    ITSELF,
    STRANGER,
    FRIEND,
    FRIEND_REQ_SENT,
    FRIEND_REQ_RECEIVED;
}