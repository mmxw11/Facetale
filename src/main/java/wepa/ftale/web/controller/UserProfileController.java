package wepa.ftale.web.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.domain.Account;
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

    @PostMapping("/api/user/sendfriendreq")
    @PreAuthorize("authentication.principal.id == #senderId")
    public String handleSendFriendRequest(@RequestParam UUID senderId, @RequestParam UUID recipientId) {
        Account recipient = userService.sendFriendRequest(senderId, recipientId);
        return "redirect:/users/" + recipient.getProfileTag();
    }
}