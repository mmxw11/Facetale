package wepa.ftale.domain.projection;

/**
 * @author Matias
 */
public class UserPostView implements PostView {

    private long postId;
    private long likeCount;
    private long commentCount;
    private boolean isPostRequesterAllowedToLike;

    public UserPostView(PostView postView, boolean postRequesterAllowedToLike) {
        this.postId = postView.getPostId();
        this.likeCount = postView.getLikeCount();
        this.commentCount = postView.getCommentCount();
        this.isPostRequesterAllowedToLike = postRequesterAllowedToLike;
    }

    @Override
    public long getPostId() {
        return postId;
    }

    @Override
    public long getLikeCount() {
        return likeCount;
    }

    @Override
    public long getCommentCount() {
        return commentCount;
    }

    public boolean isPostRequesterAllowedToLike() {
        return isPostRequesterAllowedToLike;
    }
}