package wepa.ftale.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
*
* @author Matias
*/
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Image extends AbstractPersistable<UUID> {

    private Long contentLength;
    private String contentType;
    @Lob
    private byte[] data;
}