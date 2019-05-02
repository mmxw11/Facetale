package wepa.ftale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.FriendRequest;

/**
 * @author Matias
 */
public interface FriendRepository extends JpaRepository<FriendRequest, Long> {

    /**
     * Find friend request that is either accepted or pending.
     * The sender and recipient can be either of the accounts but they both must match at least one of them.
     * @param account
     * @param account2
     * @return FriendRequest
     */
    @Query("SELECT r FROM FriendRequest r WHERE ((r.sender = :account OR r.recipient = :account) AND (r.sender = :account2 OR r.recipient = :account2)) AND (frequestStatus = 0 OR frequestStatus = 2)")
    FriendRequest findActiveFriendRequest(Account account, Account account2);
}