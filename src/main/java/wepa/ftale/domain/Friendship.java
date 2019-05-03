package wepa.ftale.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
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
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Friendship extends AbstractPersistable<Long> {

    private LocalDateTime creationDate;
    /** 
     * Whether this friendship is active. (users are friends)
     * False means that the users are not friend with each other but a request has been sent. (friend request pending)
     * If the friendship is rejected or later revoked (user removes the target user from their friend list),
     * the entire database record will be deleted.
     * */
    private boolean active;
    @ManyToOne
    private Account initiator;
    @ManyToOne
    private Account target;
}