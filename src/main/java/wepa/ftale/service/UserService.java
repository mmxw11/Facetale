package wepa.ftale.service;

import java.time.LocalDateTime;
import java.util.HashMap;
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

import wepa.ftale.domain.Account;
import wepa.ftale.domain.Friendship;
import wepa.ftale.domain.Post;
import wepa.ftale.domain.UserPostView;
import wepa.ftale.repository.AccountRepository;
import wepa.ftale.repository.FriendRepository;
import wepa.ftale.repository.PostRepository;
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
    private PostRepository postRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private MessageService messageService;

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
        Account requester = accountRepository.getOne(getAuthenticatedUser().getId());
        UserRelationship urelationship = findUserRelationship(requester, account);
        Pageable pageable = PageRequest.of(0, 4, Sort.by("creationDate").descending());
        Page<Post> posts = messageService.getProfilePosts(account, pageable);
       // Post pp = posts.getContent().get(0);
      //  pp.getLikes().add(requester);
      //  postRepository.save(pp);
        List<UserPostView> postViews = postRepository.fetchUserPostViews(requester, posts.getContent());
        Map<Long, UserPostView> userPostViews = new HashMap<>();
        for (UserPostView v : postViews) {
            userPostViews.put(v.getPostId(), v);
        }/*
        System.out.println("userPostViews: " + userPostViews);
        postViews.forEach(e -> System.out.println("lieks: " + e.getLikeCount() + " | " + e.isPostRequesterAllowedToLike() + " | " + e.getPostId()));
       */
        /**
        Post pp = posts.get().findFirst().get();
        pp.getLikes().add(requester);
        postRepository.save(pp);
        posts = messageService.getProfilePostsForRequester(account, requester, pageable);*/
        // posts.forEach(p -> System.out.println("hasPostRequesterLiked: " +
        // p.getHasPostRequesterLiked()));
        // new PageImpl<>(new ArrayList<>());
        /* posts.forEach(System.out::println); */
        // List<Post> posts = new ArrayList<>();
        /**Account jorma = accountRepository.findByUsername("jorma");
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            Post post = new Post();
            post.setAuthor(r.nextBoolean() ? jorma : account);
            post.setTarget(account);
            post.setTextContent(i
                    + ". Lorem Ipsum is simply dummy text of the printing and typesetting industry. "
                    + "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, "
                    + "when an unknown printer took a galley of type and scrambled it to make a type "
                    + "specimen book. It has survived not only five centuries, but also the leap into "
                    + "electronic typesetting, remaining essentially unchanged. It was popularised in the "
                    + "1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more "
                    + "recently with desktop publishing software like Aldus PageMaker including versions of "
                    + "Lorem Ipsum.");
            posts.add(post);
        }*/
        ProfileModel profileModel = new ProfileModel(account, displayType, urelationship, posts, userPostViews);
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