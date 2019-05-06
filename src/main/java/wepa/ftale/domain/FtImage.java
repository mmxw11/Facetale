package wepa.ftale.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
* @author Matias
*/
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class FtImage extends AbstractPersistable<UUID> {

    @NotNull
    @ManyToOne
    private Account uploader;
    @NotNull
    private Long contentLength;
    @NotBlank
    private String contentType;
    @NotNull
    @Lob
    private byte[] data;

    public String getContentSubType() {
        MediaType mediaType = MediaType.parseMediaType(contentType);
        return mediaType.getSubtype();
    }
}