package wepa.ftale.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
public class Comment extends AbstractPersistable<Long> {

    @ManyToOne
    private Account author;
    @NotNull
    @ManyToOne
    private Post post;
    @NotNull
    private LocalDateTime creationDate = LocalDateTime.now();
    @Lob
    @Column
    @NotBlank
    private String textContent;
}