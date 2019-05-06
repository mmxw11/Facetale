package wepa.ftale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import wepa.ftale.domain.Comment;
import wepa.ftale.domain.Post;

/**
 * @author Matias
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPost(Post post, Pageable pageable);
}