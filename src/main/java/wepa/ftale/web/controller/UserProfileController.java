package wepa.ftale.web.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.service.UserService;
import wepa.ftale.web.profile.ProfileModel;
import wepa.ftale.web.profile.ProfileViewDisplayType;

/**
 * @author Matias
 */
@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping(path = {"/me", "/me/{displayType}"})
    public String viewUserProfile(Model model, @PathVariable Optional<String> displayType) {
        return viewUserProfile(model, null, displayType);
    }

    @GetMapping(path = {"/users/{profileTag}", "/users/{profileTag}/{displayType}"})
    public String viewUserProfile(Model model, @PathVariable String profileTag, @PathVariable Optional<String> displayType) {
        ProfileViewDisplayType pvdisplayType = displayType.isPresent() ? ProfileViewDisplayType.parse(displayType.get()) : ProfileViewDisplayType.POSTS;
        if (pvdisplayType == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "DisplayType " + displayType.get() + " not found.");
        }
        ProfileModel profileModel = userService.createProfileModel(profileTag, pvdisplayType);
        userService.updateAuthenticatedUserToModel(model);
        model.addAttribute("profileModel", profileModel);
        return "user-profile";
    }
}