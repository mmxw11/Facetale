package wepa.ftale.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import wepa.ftale.domain.Friendship;

/**
 * @author Matias
 */
public interface FriendRepository extends JpaRepository<Friendship, Long> {

    /**
     * Find friendship that is either accepted or pending.
     * The friendship initiator and target can be either of the accounts but they both must match at least one of them.
     * @param accountId
     * @param accountId2
     * @return Friendship
     */
    @Query("SELECT r FROM Friendship r WHERE (r.initiator = :accountId OR r.target = :accountId) AND (r.initiator = :accountId2 OR r.target = :accountId2)")
    Friendship findFriendship(UUID accountId, UUID accountId2);
}