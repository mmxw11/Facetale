package wepa.ftale.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import wepa.ftale.domain.Comment;

/**
 * @author Matias
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}