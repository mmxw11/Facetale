package wepa.ftale.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.SessionUser;
import wepa.ftale.repository.AccountRepository;

/**
 * @author Matias
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createAccount(Account account, BindingResult bindingResult) {
        List<Account> accounts = accountRepository.findAllByUsernameOrProfileTagAllIgnoreCase(account.getUsername(), account.getProfileTag());
        if (!accounts.isEmpty()) {
            if (accounts.size() > 2) {
                // This should be impossible to happen. If there are more than two items returned
                // that means we have multiple users with the same profile tag or username,
                // which should have never happend.
                throw new RuntimeException("Duplicated users error! Found multiple users with the same username or profile tag!");
            }
            // There's already a user or registered with the username or profile tag.
            createUnableToCreateUserResult(account, accounts.get(0), bindingResult);
            if (accounts.size() > 1) {
                createUnableToCreateUserResult(account, accounts.get(1), bindingResult);
            }
            return;
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
    }

    public boolean isUserAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    public SessionUser getSessionUser() {
        if (!isUserAuthenticated()) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (SessionUser) principal;
    }

    private void createUnableToCreateUserResult(Account account, Account facc, BindingResult bindingResult) {
        if (facc.getProfileTag().equalsIgnoreCase(account.getProfileTag())) {
            bindingResult.addError(new FieldError("account", "profileTag", "This profile tag is already used by another user."));
        }
        if (facc.getUsername().equalsIgnoreCase(account.getUsername())) {
            bindingResult.addError(new FieldError("account", "username", "This username is already used by another user."));
        }
    }
}
