package wepa.ftale.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
public class Post extends AbstractPersistable<Long> {

    @NotNull
    @ManyToOne
    private Account author;
    @NotNull
    @ManyToOne
    private Account target;
    @NotNull
    private LocalDateTime creationDate = LocalDateTime.now();
    @Type(type = "org.hibernate.type.TextType")
    @NotBlank
    private String textContent;
    @OneToOne(cascade = CascadeType.ALL)
    private FtImage image;
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "like_author_id", referencedColumnName = "id"))
    private List<Account> likes;
}