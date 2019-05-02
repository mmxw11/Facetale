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
public class FriendRequest extends AbstractPersistable<Long> {

    private LocalDateTime creationDate;
    private FriendRequestStatus frequestStatus;
    @ManyToOne
    private Account sender;
    @ManyToOne
    private Account recipient;
}
