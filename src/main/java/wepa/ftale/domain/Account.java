package wepa.ftale.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
public class Account extends AbstractPersistable<UUID> {

    @NotBlank
    @Size(min = 3, max = 20)
    @Column(unique = true)
    private String username;
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
    @NotBlank
    @Size(min = 3, max = 15)
    @Column(unique = true)
    private String profileTag;
    @NotBlank
    @Size(min = 5, max = 250)
    private String password;
    @OneToOne
    private FtImage profilePicture;
}