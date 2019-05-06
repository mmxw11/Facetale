package wepa.ftale.web.controller;

import java.io.IOException;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.domain.Comment;
import wepa.ftale.domain.Post;
import wepa.ftale.service.MessageService;
import wepa.ftale.web.FormUtils;
import wepa.ftale.web.profile.ProfileViewDisplayType;

/**
 * @author Matias
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/api/posts")
    @PreAuthorize("#page >= 0")
    public String getPosts(@RequestParam UUID target, @RequestParam ProfileViewDisplayType type, @RequestParam int page, @RequestParam int count, Model model) {
        messageService.buildProfilePostsView(target, type, page, count, model);
        return "fragments/post :: list-comments";
    }

    @GetMapping("/api/posts/comments")
    @PreAuthorize("#page >= 0")
    public String getComments(@RequestParam long postId, @RequestParam int page, @RequestParam int count, Model model) {
        messageService.buildCommentsView(postId, page, count, model);
        return "fragments/post :: list-comments";
    }

    @PostMapping("/api/posts/addpost")
    public String handleAddPost(@Valid @ModelAttribute Post post, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FormUtils.formatBindingResultErrors(bindingResult));
        }
        messageService.addPost(post, model);
        return "fragments/post :: comment";
    }

    @PostMapping(path = "/api/posts/addimagepost", consumes = "multipart/form-data")
    @PreAuthorize("authentication.principal.id == #post.getTarget().getId()")
    public String handleAddImagePost(@Valid @ModelAttribute Post post, BindingResult bindingResult, @RequestParam MultipartFile file, Model model) throws IOException {
        if (!file.getContentType().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FormUtils.formatBindingResultErrors(bindingResult));
        }
        messageService.addPost(post, file);
        // TODO: RETURN LIST ITEM
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/api/posts/addcomment")
    public String handleAddComment(@Valid @ModelAttribute Comment comment, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FormUtils.formatBindingResultErrors(bindingResult));
        }
        messageService.addComment(comment, model);
        return "fragments/post :: comment";
    }

    @PostMapping(path = "/api/posts/deleteimagepost")
    @PreAuthorize("authentication.principal.id == #accountId")
    public ResponseEntity<?> handleDeleteImagePost(@RequestParam UUID accountId, @RequestParam Long postId) {
        messageService.deleteImagePost(accountId, postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/api/posts/likepost")
    @PreAuthorize("authentication.principal.id == #accountId")
    public ResponseEntity<?> handleLikePost(@RequestParam UUID accountId, @RequestParam Long postId) {
        messageService.likePost(accountId, postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/api/posts/removelike")
    @PreAuthorize("authentication.principal.id == #accountId")
    public ResponseEntity<?> handleRemoveLike(@RequestParam UUID accountId, @RequestParam Long postId) {
        messageService.removeLike(accountId, postId);
        return ResponseEntity.ok().build();
    }
}