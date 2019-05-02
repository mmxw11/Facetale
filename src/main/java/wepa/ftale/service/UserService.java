package wepa.ftale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.domain.Account;
import wepa.ftale.repository.AccountRepository;
import wepa.ftale.web.AuthenticatedUser;
import wepa.ftale.web.profile.ProfileModel;
import wepa.ftale.web.profile.ProfileViewDisplayType;
import wepa.ftale.web.profile.UserRelationship;

/**
 * @author Matias
 */
@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FriendService friendService;

    public ProfileModel createProfileModel(String profileTag, ProfileViewDisplayType displayType) throws ResponseStatusException {
        AuthenticatedUser auser = getAuthenticatedUser();
        if (profileTag == null || profileTag.equals(auser.getAccount().getProfileTag())) {
            return createProfileModel(auser.getAccount(), displayType);
        }
        Account account = accountRepository.findByProfileTag(profileTag);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        }
        return createProfileModel(account, displayType);
    }

    public ProfileModel createProfileModel(Account account, ProfileViewDisplayType displayType) {
        UserRelationship urelationship = friendService.findUserRelationship(getAuthenticatedUser().getAccount(), account);
        ProfileModel profileModel = new ProfileModel(account, displayType, urelationship);
        return profileModel;
    }

    public void updateAuthenticatedUserToModel(Model model) {
        AuthenticatedUser auser = getAuthenticatedUser();
        if (auser == null) {
            return;
        }
        model.addAttribute("auser", auser);
    }

    public boolean isUserAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    public AuthenticatedUser getAuthenticatedUser() {
        if (!isUserAuthenticated()) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (AuthenticatedUser) principal;
    }
}