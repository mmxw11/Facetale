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

import wepa.ftale.domain.Image;
import wepa.ftale.repository.ImageRepository;

/**
 * @author Matias
 */
@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @PostConstruct
    public void addImage() throws IOException {
        byte[] bytes = Files.readAllBytes(
                Paths.get("src/main/resources/static/images/export.png"));
        Image image = new Image((long) bytes.length, "image/png", bytes);
        imageRepository.save(image);
        System.out.println("UUID: " + image.getId());
    }

    public ResponseEntity<byte[]> getImage(UUID id, String type) {
        Optional<Image> oimage = imageRepository.findById(id);
        if (!oimage.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Image image = oimage.get();
        MediaType mediaType = MediaType.parseMediaType(image.getContentType());
        if (!mediaType.getSubtype().equals(type)) {
            byte[] msg = "Image signature does not match!".getBytes();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(msg.length)
                    .body(msg);
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(image.getContentLength())
                .body(image.getData());
    }
}