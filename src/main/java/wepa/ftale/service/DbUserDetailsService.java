package wepa.ftale.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import wepa.ftale.domain.Account;
import wepa.ftale.repository.AccountRepository;
import wepa.ftale.web.AuthenticatedUser;

/**
 * @author Matias
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        return new AuthenticatedUser(account, Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}