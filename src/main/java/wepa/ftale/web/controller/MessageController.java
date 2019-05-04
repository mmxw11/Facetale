package wepa.ftale.web.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.domain.Post;
import wepa.ftale.service.MessageService;
import wepa.ftale.web.FormUtils;

/**
 * @author Matias
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/api/posts/addpost")
    @ResponseStatus(HttpStatus.CREATED)
    public String handleAddPost(@Valid @ModelAttribute Post post, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FormUtils.formatBindingResultErrors(bindingResult));
        }
        messageService.addPost(post);
        // TODO: RETURN LIST ITEMs
        model.addAttribute("test", "attributti!");
        return "fragments/post :: test-fragment";
    }

    @PostMapping(path = "/api/posts/addimagepost", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("authentication.principal.id == #post.getTargetUser().getId()")
    public String handleAddImagePost(@Valid @ModelAttribute Post post, BindingResult bindingResult, @RequestParam MultipartFile file, Model model) throws IOException {
        if (!file.getContentType().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FormUtils.formatBindingResultErrors(bindingResult));
        }
        messageService.addPost(post, file);
        // TODO: RETURN LIST ITEM
        model.addAttribute("test", "attributti!");
        return "fragments/post :: test-fragment";
    }
    // TODO: REMOVE IMAGEPOST
    // TODO: ADD COMMENT
}