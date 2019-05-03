package wepa.ftale.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.FtImage;
import wepa.ftale.repository.ImageRepository;
import wepa.ftale.web.AuthenticatedUser;

/**
 * @author Matias
 */
@Service("imageService")
public class ImageService {

    private static final String DEFAULT_PROFILE_PICTURE = "/static/images/Portrait_Placeholder.png";
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserService userService;

    @PostConstruct
    public void addImage() throws IOException {
        byte[] bytes = Files.readAllBytes(
                Paths.get("src/main/resources/static/images/export.png"));
        FtImage image = new FtImage((long) bytes.length, "image/png", bytes);
        imageRepository.save(image);
        System.out.println("UUID: " + image.getId());
    }

    public String getUserProfilePictureLocation(Account account) {
        FtImage profilePicture = account.getProfilePicture();
        if (profilePicture == null) {
            return DEFAULT_PROFILE_PICTURE;
        }
        MediaType mediaType = MediaType.parseMediaType(profilePicture.getContentType());
        String fullImageSignature = profilePicture.getId().toString() + "." + mediaType.getSubtype();
        return "/api/images/" + fullImageSignature;
    }

    public String getAuthenticatedUserProfilePictureLocation() {
        AuthenticatedUser auser = userService.getAuthenticatedUser();
        FtImage profilePicture = auser.getProfilePicture();
        if (profilePicture == null) {
            return DEFAULT_PROFILE_PICTURE;
        }
        MediaType mediaType = MediaType.parseMediaType(profilePicture.getContentType());
        return "/api/auser/profile-picture" + "." + mediaType.getSubtype();
    }

    public ResponseEntity<byte[]> getAuthenticatedUserProfilePicture(String type) {
        AuthenticatedUser auser = userService.getAuthenticatedUser();
        FtImage ftImage = auser.getProfilePicture();
        if (ftImage == null) {
            return ResponseEntity.notFound().build();
        }
        return createImageResponse(ftImage, type);
    }

    public ResponseEntity<byte[]> getImage(UUID id, String type) {
        Optional<FtImage> oimage = imageRepository.findById(id);
        if (!oimage.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        FtImage ftImage = oimage.get();
        return createImageResponse(ftImage, type);
    }

    private ResponseEntity<byte[]> createImageResponse(FtImage ftImage, String type) {
        MediaType mediaType = MediaType.parseMediaType(ftImage.getContentType());
        if (!mediaType.getSubtype().equals(type)) {
            byte[] msg = "Image signature does not match!".getBytes();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(msg.length)
                    .body(msg);
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(ftImage.getContentLength())
                .body(ftImage.getData());
    }
}