package wepa.ftale.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.Post;
import wepa.ftale.domain.projection.PostView;

/**
 * @author Matias
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT COUNT(*) FROM Post WHERE author = :account AND image IS NOT NULL")
    long albumImageCount(Account account);

    Page<Post> findAllByTargetAndImageIsNull(Account target, Pageable pageable);

    Page<Post> findAllByTargetAndImageIsNotNull(Account target, Pageable pageable);

    @Query(value = "SELECT Post.id AS postId, COUNT(DISTINCT pl.like_author_id) AS likeCount, COUNT(DISTINCT c.id) AS commentCount "
            + "FROM Post LEFT JOIN Comment c ON c.post_id = Post.id "
            + "LEFT JOIN Post_likes pl ON pl.post_id = Post.id "
            + "WHERE Post.id IN (:posts) "
            + "GROUP BY Post.id", nativeQuery = true)
    List<PostView> fetchPostViews(List<Post> posts);

    @Query(value = "SELECT post_id FROM Post_likes WHERE like_author_id = :user AND post_id IN (:posts)", nativeQuery = true)
    List<BigInteger> getLikedPostIds(Account user, List<Post> posts);
}