package wepa.ftale.domain;

/**
 * @author Matias
 */
public interface UserPostView {

    Long getPostId();

    long getLikeCount();

    long getCommentCount();

    boolean isPostRequesterAllowedToLike();
}