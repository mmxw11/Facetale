package wepa.ftale.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.Friendship;
import wepa.ftale.repository.AccountRepository;
import wepa.ftale.repository.FriendRepository;
import wepa.ftale.web.AuthenticatedUser;
import wepa.ftale.web.profile.ProfileModel;
import wepa.ftale.web.profile.ProfileViewDisplayType;
import wepa.ftale.web.profile.UserRelationship;

/**
 * @author Matias
 */
@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FriendRepository friendRepository;

    public ProfileModel createProfileModel(String profileTag, ProfileViewDisplayType displayType) throws ResponseStatusException {
        AuthenticatedUser auser = getAuthenticatedUser();
        if (profileTag == null || profileTag.equals(auser.getProfileTag())) {
            profileTag = auser.getProfileTag();
        }
        Account account = accountRepository.findByProfileTag(profileTag);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        }
        return createProfileModel(account, displayType);
    }

    public ProfileModel createProfileModel(Account account, ProfileViewDisplayType displayType) {
        UserRelationship urelationship = findUserRelationship(accountRepository.getOne(getAuthenticatedUser().getId()), account);
        ProfileModel profileModel = new ProfileModel(account, displayType, urelationship);
        return profileModel;
    }

    @Transactional
    @PreAuthorize("#senderId != #recipientId")
    public Account sendFriendRequest(UUID senderId, UUID recipientId) throws ResponseStatusException {
        Account sender = accountRepository.getOne(senderId);
        Account recipient = accountRepository.getOne(recipientId);
        if (friendRepository.findFriendship(sender, recipient) != null) {
            // Users are already friends or there is an active friend request.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        Friendship friendShip = new Friendship(LocalDateTime.now(), false, sender, recipient);
        friendRepository.save(friendShip);
        return recipient;
    }

    /**
     * Find the relationship between two users.
     * Mainly used to determine whether currently logged user is friends with the user whose profile they are viewing.
     * @param account
     * @param target
     * @return UserRelationship
     */
    public UserRelationship findUserRelationship(Account account, Account target) {
        if (account.equals(target)) {
            return UserRelationship.ITSELF;
        }
        Friendship friendship = friendRepository.findFriendship(account, target);
        if (friendship == null) {
            return UserRelationship.STRANGER;
        }
        if (friendship.isActive()) {
            return UserRelationship.FRIEND;
        }
        // There's a pending friend request.
        return friendship.getInitiator().equals(account) ? UserRelationship.FRIEND_REQ_SENT : UserRelationship.FRIEND_REQ_RECEIVED;
    }

    public void updateAuthenticatedUserToModel(Model model) {
        AuthenticatedUser auser = getAuthenticatedUser();
        if (auser == null) {
            return;
        }
        model.addAttribute("auser", auser);
    }

    public boolean isUserAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    public Account getAuthenticatedUserAccount() {
        return accountRepository.getOne(getAuthenticatedUser().getId());
    }

    public AuthenticatedUser getAuthenticatedUser() {
        if (!isUserAuthenticated()) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (AuthenticatedUser) principal;
    }
}