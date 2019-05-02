package wepa.ftale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import wepa.ftale.web.AuthenticatedUser;

/**
 * @author Matias
 */
@Service
public class UserService {

    @Autowired
    private ImageService imageService;

    public void updateAuthenticatedUserToModel(Model model) {
        AuthenticatedUser auser = getAuthenticatedUser();
        if (auser == null) {
            return;
        }
        String profPicLocation = imageService.parseProfilePictureLocation(auser.getAccount());
        model.addAttribute("auser", auser);
        model.addAttribute("auserProfPicLocation", profPicLocation);
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