package wepa.ftale.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.domain.AbstractPersistable;

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

    @ManyToOne
    private Account uploader;
    private Long contentLength;
    private String contentType;
    @Lob
    private byte[] data;
}