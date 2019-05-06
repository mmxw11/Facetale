package wepa.ftale.service;

import java.io.IOException;
import java.util.Arrays;
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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.Pair;
import wepa.ftale.domain.Account;
import wepa.ftale.domain.FtImage;
import wepa.ftale.domain.Post;
import wepa.ftale.domain.UserPostView;
import wepa.ftale.repository.ImageRepository;
import wepa.ftale.repository.PostRepository;
import wepa.ftale.web.profile.ProfileViewDisplayType;
import wepa.ftale.web.profile.UserRelationship;

/**
 * @author Matias
 */
@Service
public class MessageService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public void addPost(Post post, Model model) throws ResponseStatusException {
        post.setAuthor(userService.getAuthenticatedUserAccount());
        checkFriendPermissions(post.getAuthor(), post.getTarget());
        postRepository.save(post);
        updateNewPostModel(post, model);
    }

    @Transactional
    @PostAuthorize("#post.getAuthor().equals(#post.getTarget())")
    public void addPost(Post post, MultipartFile file) throws ResponseStatusException, IOException {
        post.setAuthor(userService.getAuthenticatedUserAccount());
        long imageCount = postRepository.albumImageCount(post.getTarget());
        if (imageCount >= 10) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Image limit of 10 exceeded!");
        }
        FtImage image = new FtImage(post.getTarget(), file.getSize(), file.getContentType(), file.getBytes());
        post.setImage(image);
        imageRepository.save(image);
        postRepository.save(post);
    }

    @Transactional
    public void deleteImagePost(UUID accountId, Long postId) {
        Post post = postRepository.getOne(postId);
        FtImage image = post.getImage();
        if (image == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This post does not contain an image!");
        }
        Account account = userService.getAccount(accountId);
        if (!post.getAuthor().equals(account)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the author!");
        }
        // Reset profile picture.
        if (account.getProfilePicture() != null && account.getProfilePicture().equals(image)) {
            account.setProfilePicture(null);
        }
        userService.saveAccount(account);
        postRepository.delete(post);
    }

    @Transactional
    public void likePost(UUID accountId, Long postId) throws ResponseStatusException {
        Post post = postRepository.getOne(postId);
        Account account = userService.getAccount(accountId);
        checkFriendPermissions(account, post.getTarget());
        if (post.getLikes().contains(account)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't like same post twice!");
        }
        post.getLikes().add(account);
        postRepository.save(post);
    }

    @Transactional
    public void removeLike(UUID accountId, Long postId) {
        Post post = postRepository.getOne(postId);
        Account account = userService.getAccount(accountId);
        if (!post.getLikes().contains(account)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't have a like on this post!");
        }
        post.getLikes().remove(account);
        postRepository.save(post);
    }

    public void buildProfilePostsView(UUID target, ProfileViewDisplayType type, int page, int count, Model model) {
        Account requester = userService.getAuthenticatedUserAccount();
        Account targetAccount = userService.getAccount(target);
        UserRelationship urelationship = userService.findUserRelationship(requester, targetAccount);
        Pageable pageable = PageRequest.of(page, count, Sort.by("creationDate").descending());
        Pair<Page<Post>, Map<Long, UserPostView>> postsPair = generateRequesterPostViews(targetAccount, requester, type, pageable);
        model.addAttribute("auserRelationship", urelationship);
        model.addAttribute("post", true);
        model.addAttribute("comments", postsPair.getKey());
        model.addAttribute("viewMap", postsPair.getValue());
    }

    public Page<Post> getProfilePosts(Account target, ProfileViewDisplayType pvDisplayType, Pageable pageable) {
        if (pvDisplayType == ProfileViewDisplayType.POSTS) {
            return postRepository.findAllByTarget(target, pageable);
        } else if (pvDisplayType == ProfileViewDisplayType.ALBUM) {
            return postRepository.findAllImageNotNullByTarget(target, pageable);
        }
        throw new RuntimeException("Unsupported DisplayType '" + pvDisplayType + "'!");
    }

    public Pair<Page<Post>, Map<Long, UserPostView>> generateRequesterPostViews(Account account, Account requester, ProfileViewDisplayType pvDisplayType, Pageable pageable) {
        Page<Post> posts = getProfilePosts(account, pvDisplayType, pageable);
        List<UserPostView> postViews = postRepository.fetchUserPostViews(requester, posts.getContent());
        Map<Long, UserPostView> userPostViews = new HashMap<>();
        for (UserPostView v : postViews) {
            userPostViews.put(v.getPostId(), v);
        }
        return new Pair<>(posts, userPostViews);
    }

    private void updateNewPostModel(Post post, Model model) {
        UserRelationship urelationship = userService.findUserRelationship(post.getAuthor(), post.getTarget());
        List<UserPostView> postViews = postRepository.fetchUserPostViews(post.getAuthor(), Arrays.asList(post));
        model.addAttribute("comment", post);
        model.addAttribute("view", !postViews.isEmpty() ? postViews.get(0) : null);
        model.addAttribute("post", true);
        model.addAttribute("auserRelationship", urelationship);
    }

    private void checkFriendPermissions(Account account, Account target) throws ResponseStatusException {
        UserRelationship urelationship = userService.findUserRelationship(account, target);
        if (urelationship != UserRelationship.ITSELF && urelationship != UserRelationship.FRIEND) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}