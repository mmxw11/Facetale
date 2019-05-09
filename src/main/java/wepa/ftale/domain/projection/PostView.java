package wepa.ftale.domain.projection;

/**
 * @author Matias
 */
public interface PostView {

    long getPostId();

    long getLikeCount();

    long getCommentCount();
}