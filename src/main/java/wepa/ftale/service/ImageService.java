package wepa.ftale.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Transactional
    public void setUserProfilePicture(UUID accountId, UUID imageId) throws ResponseStatusException {
        Account account = userService.getAccount(accountId);
        FtImage ftImage = null;
        if (imageId != null) {
            ftImage = imageRepository.getOne(imageId);
            if (!ftImage.getUploader().equals(account)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else if (account.getProfilePicture() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        account.setProfilePicture(ftImage);
        AuthenticatedUser auser = userService.getAuthenticatedUser();
        if (auser.getId().equals(accountId)) {
            auser.setProfilePicture(ftImage);
        }
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
        return "/api/user/profile-picture" + "." + mediaType.getSubtype();
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