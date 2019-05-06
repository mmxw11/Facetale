package wepa.ftale.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import wepa.ftale.domain.Account;

/**
 * @author Matias
 */
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Account findByUsername(String username);

    Account findByProfileTagIgnoreCase(String profileTag);

    Page<Account> findAllByNameIgnoreCaseContaining(String name, Pageable pageable);

    List<Account> findAllByUsernameOrProfileTagAllIgnoreCase(String username, String profileTag);
}