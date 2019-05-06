package wepa.ftale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.Friendship;

/**
 * @author Matias
 */
public interface FriendRepository extends JpaRepository<Friendship, Long> {

    /**
     * Find friendship that is either accepted or pending.
     * The friendship initiator and target can be either of the accounts but they both must match at least one of them.
     * @param account
     * @param account2
     * @return Friendship
     */
    @Query("SELECT r FROM Friendship r WHERE (r.initiator = :account OR r.target = :account) AND (r.initiator = :account2 OR r.target = :account2)")
    Friendship findFriendship(Account account, Account account2);

    List<Friendship> findAllByInitiatorOrTargetAndActiveTrue(Account initiator, Account target);
    
    List<Friendship> findAllByInitiatorOrTarget(Account initiator, Account target);
}