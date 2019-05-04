package wepa.ftale.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Matias
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Post extends AbstractPersistable<UUID> {

    @ManyToOne
    private Account author;
    @NotNull
    @ManyToOne
    private Account targetUser;
    @NotNull
    private LocalDateTime creationDate = LocalDateTime.now();
    @NotBlank
    private String textContent;
    @OneToOne
    private FtImage image;
}