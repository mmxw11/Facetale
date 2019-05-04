package wepa.ftale.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.Post;

/**
 * @author Matias
 */
public interface PostRepository extends JpaRepository<Post, UUID> {
    
    @Query("SELECT COUNT(*) FROM Post WHERE author = :account AND image != null")
    long albumImageCount(Account account);
}