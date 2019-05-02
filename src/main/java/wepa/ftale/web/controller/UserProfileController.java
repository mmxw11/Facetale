package wepa.ftale.web.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import wepa.ftale.service.UserService;
import wepa.ftale.web.profile.AuthenticatedUserRelationship;
import wepa.ftale.web.profile.ProfileModel;
import wepa.ftale.web.profile.ProfileViewDisplayType;

/**
 * @author Matias
 */
@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping(path = {"/me", "/me/{page:posts|album}"})
    public String viewUserProfile(Model model, @PathVariable Optional<String> page) {
        return viewUserProfile(model, null, page);
    }

    @GetMapping(path = {"/users/{profileTag}", "/users/{profileTag}/{page:posts|album}"})
    public String viewUserProfile(Model model, @PathVariable String profileTag, @PathVariable Optional<String> page) {
        ProfileViewDisplayType displayType = page.isPresent() ? ProfileViewDisplayType.parse(page.get()) : ProfileViewDisplayType.POSTS;
        ProfileModel profileModel = new ProfileModel(userService.getAuthenticatedUser().getAccount(), displayType, AuthenticatedUserRelationship.ITSELF);
        userService.updateAuthenticatedUserToModel(model);
        model.addAttribute("profileModel", profileModel);
        return "user-profile";
    }
}