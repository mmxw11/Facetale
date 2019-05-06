package wepa.ftale.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.Post;
import wepa.ftale.domain.UserPostView;

/**
 * @author Matias
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT COUNT(*) FROM Post WHERE author = :account AND image IS NOT NULL")
    long albumImageCount(Account account);

    Page<Post> findAllByTarget(Account target, Pageable pageable);
    
    Page<Post> findAllImageNotNullByTarget(Account target, Pageable pageable);

    @Query(value = "SELECT Post.id AS postId, IsNull(COUNT(post_likes.post_id), 0) AS likeCount, IsNull((SELECT CASE WHEN post_likes.like_author_id = :requester THEN false ELSE true end), true) AS postRequesterAllowedToLike FROM Post"
            + " LEFT JOIN post_likes ON post_likes.post_id = Post.id WHERE Post.id IN (:posts) GROUP BY Post.id", nativeQuery = true)
    List<UserPostView> fetchUserPostViews(Account requester, List<Post> posts);
}