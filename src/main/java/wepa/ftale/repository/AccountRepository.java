package wepa.ftale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import wepa.ftale.domain.Account;

/**
 * @author Matias
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);

    Account findByProfileTag(String profileTag);

    List<Account> findAllByUsernameOrProfileTagAllIgnoreCase(String username, String profileTag);
}