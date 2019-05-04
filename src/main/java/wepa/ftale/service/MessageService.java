package wepa.ftale.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.FtImage;
import wepa.ftale.domain.Post;
import wepa.ftale.repository.ImageRepository;
import wepa.ftale.repository.PostRepository;
import wepa.ftale.web.profile.UserRelationship;

@Service
public class MessageService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public void addPost(Post post) throws ResponseStatusException {
        post.setAuthor(userService.getAuthenticatedUserAccount());
        checkFriendPermissions(post.getAuthor(), post.getTargetUser());
        postRepository.save(post);
    }

    @Transactional
    @PostAuthorize("#post.getAuthor().equals(#post.getTargetUser())")
    public void addPost(Post post, MultipartFile file) throws ResponseStatusException, IOException {
        post.setAuthor(userService.getAuthenticatedUserAccount());
        long imageCount = postRepository.albumImageCount(post.getTargetUser());
        if (imageCount >= 10) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Image limit of 10 exceeded!");
        }
        FtImage image = new FtImage(post.getTargetUser(), file.getSize(), file.getContentType(), file.getBytes());
        post.setImage(image);
        imageRepository.save(image);
        postRepository.save(post);
    }

    private void checkFriendPermissions(Account account, Account target) throws ResponseStatusException {
        UserRelationship urelationship = userService.findUserRelationship(account, target);
        if (urelationship != UserRelationship.ITSELF && urelationship != UserRelationship.FRIEND) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}