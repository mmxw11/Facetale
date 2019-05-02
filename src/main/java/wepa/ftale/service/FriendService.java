package wepa.ftale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.FriendRequest;
import wepa.ftale.domain.FriendRequestStatus;
import wepa.ftale.repository.FriendRepository;
import wepa.ftale.web.profile.UserRelationship;

/**
 * @author Matias
 */
@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    /**
     * Find the relationship between two users.
     * Mainly used to determine whether currently logged user is friends with the user whose profile they are viewing.
     * @param auser
     * @param target
     * @return UserRelationship
     */
    public UserRelationship findUserRelationship(Account account, Account target) {
        if (account.equals(target)) {
            return UserRelationship.ITSELF;
        }
        FriendRequest friendRequest = friendRepository.findActiveFriendRequest(account, target);
        if (friendRequest == null) {
            return UserRelationship.STRANGER;
        }
        if (friendRequest.getFrequestStatus() == FriendRequestStatus.ACCEPTED) {
            return UserRelationship.FRIEND;
        }
        // There's a pending friend request.
        return friendRequest.getSender().equals(account) ? UserRelationship.FRIEND_REQ_SENT : UserRelationship.FRIEND_REQ_RECEIVED;
    }
}