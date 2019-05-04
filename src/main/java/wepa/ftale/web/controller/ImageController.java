package wepa.ftale.web.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import wepa.ftale.service.ImageService;

/**
 * @author Matias
 */
@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/api/images/{id}.{type}")
    public ResponseEntity<byte[]> viewImage(@PathVariable UUID id, @PathVariable String type) {
        return imageService.getImage(id, type);
    }

    @GetMapping("/api/user/profile-picture.{type}")
    public ResponseEntity<byte[]> viewAuthenticatedUserProfilePicture(@PathVariable String type) {
        return imageService.getAuthenticatedUserProfilePicture(type);
    }

    @PostMapping("/api/user/setprofilepic")
    @PreAuthorize("authentication.principal.id == #accountId")
    public String handleSetUserProfilePicture(@RequestParam UUID accountId, @RequestParam UUID imageId) {
        imageService.setUserProfilePicture(accountId, imageId);
        return "redirect:/me";
    }

    @PostMapping("/api/user/resetprofilepic")
    @PreAuthorize("authentication.principal.id == #accountId")
    public String handleResetUserProfilePicture(@RequestParam UUID accountId) {
        imageService.setUserProfilePicture(accountId, null);
        return "redirect:/me";
    }
}