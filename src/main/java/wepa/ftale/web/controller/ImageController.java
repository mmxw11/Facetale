package wepa.ftale.web.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/api/auser/profile-picture.{type}")
    public ResponseEntity<byte[]> viewAuthenticatedUsserProfilePicture(@PathVariable String type) {
        return imageService.getAuthenticatedUserProfilePicture(type);
    }
}