package wepa.ftale.domain.projection;

/**
 * @author Matias
 */
public class EmptyPostViewImpl implements PostView {

    private long postId;

    public EmptyPostViewImpl(long postId) {
        this.postId = postId;
    }

    @Override
    public long getPostId() {
        return postId;
    }

    @Override
    public long getLikeCount() {
        return 0;
    }

    @Override
    public long getCommentCount() {
        return 0;
    }
}