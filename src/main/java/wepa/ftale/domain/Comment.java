package wepa.ftale.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Matias
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment extends AbstractPersistable<Long> {

    @NotNull
    @ManyToOne
    private Account author;
    @NotNull
    @ManyToOne
    private Post post;
    @NotNull
    private LocalDateTime creationDate = LocalDateTime.now();
    @Type(type = "org.hibernate.type.TextType")
    @NotBlank
    private String textContent;
}