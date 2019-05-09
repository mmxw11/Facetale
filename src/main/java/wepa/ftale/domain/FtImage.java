package wepa.ftale.domain;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* @author Matias
*/
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FtImage extends AbstractPersistable<UUID> {

    @NotNull
    @ManyToOne
    private Account uploader;
    @NotNull
    private Long contentLength;
    @NotBlank
    private String contentType;
    @Type(type = "org.hibernate.type.ImageType")
    @NotNull
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    public String getContentSubType() {
        MediaType mediaType = MediaType.parseMediaType(contentType);
        return mediaType.getSubtype();
    }
}