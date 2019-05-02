package wepa.ftale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import wepa.ftale.domain.UserSession;

/**
 * @author Matias
 */
@Service
public class UserProfileService {

    @Autowired
    private ImageService imageService;

    public void updateUserSessionModel(Model model) {
        UserSession userSession = getUserSession();
        if (userSession == null) {
            return;
        }
        String profPicLocation = imageService.parseProfilePictureLocation(userSession.getAccount());
        model.addAttribute("userSession", userSession);
        model.addAttribute("userSessionProfPicLocation", profPicLocation);
    }

    public boolean isUserAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    public UserSession getUserSession() {
        if (!isUserAuthenticated()) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (UserSession) principal;
    }
}