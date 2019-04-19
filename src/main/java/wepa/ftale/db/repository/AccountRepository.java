package wepa.ftale.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import wepa.ftale.domain.Account;

/**
 * @author Matias
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
}