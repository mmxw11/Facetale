package wepa.ftale.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Matias
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Friendship extends AbstractPersistable<Long> {

    @NotNull
    private LocalDateTime creationDate;
    /** 
     * Whether this friendship is active. (users are friends)
     * False means that the users are not friend with each other but a request has been sent. (friend request pending)
     * If the friendship is rejected or later revoked (user removes the target user from their friend list),
     * the entire database record will be deleted.
     * */
    @NotNull
    private boolean active;
    @NotNull
    @ManyToOne
    private Account initiator;
    @NotNull
    @ManyToOne
    private Account target;
}