package wepa.ftale.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.Pair;
import wepa.ftale.domain.Account;
import wepa.ftale.domain.Friendship;
import wepa.ftale.domain.Post;
import wepa.ftale.domain.UserPostView;
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
    @Autowired
    private MessageService messageService;

    public ProfileModel createProfileModel(String profileTag, ProfileViewDisplayType displayType) throws ResponseStatusException {
        if (profileTag == null) {
            AuthenticatedUser auser = getAuthenticatedUser();
            profileTag = auser.getProfileTag();
        }
        Account account = accountRepository.findByProfileTagIgnoreCase(profileTag);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        }
        return createProfileModel(account, displayType);
    }

    public ProfileModel createProfileModel(Account account, ProfileViewDisplayType displayType) {
        Account requester = accountRepository.getOne(getAuthenticatedUser().getId());
        UserRelationship urelationship = findUserRelationship(requester, account);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("creationDate").descending());
        Pair<Page<Post>, Map<Long, UserPostView>> postsPair = messageService.generateRequesterPostViews(account, requester, displayType, pageable);
        ProfileModel profileModel = new ProfileModel(account, displayType, urelationship, postsPair.getKey(), postsPair.getValue());
        return profileModel;
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
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

    @Transactional
    public Account handleFriendRequest(UUID accountId, String action, long friendRequestId) throws ResponseStatusException {
        Account account = getAccount(accountId);
        Friendship friendship = friendRepository.getOne(friendRequestId);
        if (action.equals("ACCEPT")) {
            if (friendship.isActive()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are already friends!");
            } else if (!friendship.getTarget().equals(account)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This friend request was not sent to you!");
            }
            friendship.setActive(true);
            friendRepository.save(friendship);
        } else if (action.equals("DENY")) {
            if (friendship.isActive()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are already friends!");
            }
            friendRepository.delete(friendship);
        } else if (action.equals("CANCEL")) {
            if (!friendship.getInitiator().equals(account)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You didn't send this friend request!");
            }
            friendRepository.delete(friendship);
        }
        return account;
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

    public void buildFriendsView(String profileTag, Model model) throws ResponseStatusException {
        Account target = accountRepository.findByProfileTagIgnoreCase(profileTag);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        }
        Account auser = getAuthenticatedUserAccount();
        UserRelationship urelationship = findUserRelationship(auser, target);
        if (urelationship != UserRelationship.ITSELF && urelationship != UserRelationship.FRIEND) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("user", target);
        model.addAttribute("ownProfile", target.equals(auser));
        List<Account> friends = new ArrayList<>();
        if (target.equals(auser)) {
            List<Friendship> friendships = friendRepository.findAllByInitiatorOrTarget(auser, auser);
            List<Friendship> friendRequestsSent = new ArrayList<>(), friendRequestsReceived = new ArrayList<>();
            friendships.forEach(f -> {
                if (f.isActive()) {
                    friends.add(f.getInitiator().equals(auser) ? f.getTarget() : f.getInitiator());
                } else {
                    if (f.getInitiator().equals(auser)) {
                        friendRequestsSent.add(f);
                    } else {
                        friendRequestsReceived.add(f);
                    }
                }
            });
            model.addAttribute("friendRequestsSent", friendRequestsSent);
            model.addAttribute("friendRequestsReceived", friendRequestsReceived);
        } else {
            friendRepository.findAllByInitiatorOrTargetAndActiveTrue(target, target)
                    .forEach(f -> friends.add(f.getInitiator().equals(target) ? f.getTarget() : f.getInitiator()));
        }
        model.addAttribute("friends", friends);
    }

    public void updateAuthenticatedUserToModel(Model model) {
        AuthenticatedUser auser = getAuthenticatedUser();
        if (auser == null) {
            return;
        }
        model.addAttribute("auser", auser);
    }

    public Account getAccount(UUID id) {
        return accountRepository.getOne(id);
    }

    public Page<Account> findUsersWithSimilarName(String name) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by("name").ascending());
        return accountRepository.findAllByNameIgnoreCaseContaining(name, pageable);
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