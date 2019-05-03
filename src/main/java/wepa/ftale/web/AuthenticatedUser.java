package wepa.ftale.web;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import lombok.Data;
import lombok.EqualsAndHashCode;
import wepa.ftale.domain.Account;
import wepa.ftale.domain.FtImage;

/**
 * @author Matias
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthenticatedUser extends User {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private UUID id;
    private String profileTag;
    private FtImage profilePicture;

    public AuthenticatedUser(Account account, Collection<? extends GrantedAuthority> authorities) {
        this(account, true, true, true, true, authorities);
    }

    public AuthenticatedUser(Account account, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = account.getId();
        this.profileTag = account.getProfileTag();
        this.profilePicture = account.getProfilePicture();
    }
}