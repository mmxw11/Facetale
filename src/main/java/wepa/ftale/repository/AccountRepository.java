package wepa.ftale.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import wepa.ftale.domain.Account;

/**
 * @author Matias
 */
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Account findByUsername(String username);

    Account findByProfileTag(String profileTag);

    List<Account> findAllByUsernameOrProfileTagAllIgnoreCase(String username, String profileTag);
}