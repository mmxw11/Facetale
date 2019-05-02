package wepa.ftale.web.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import wepa.ftale.domain.SessionUser;
import wepa.ftale.service.AccountService;

/**
 * @author Matias
 */
@Controller
public class UserProfileController {

    @Autowired
    private AccountService accountService;

    @GetMapping(path = {"/me", "/me/{page}"})
    public String viewUserProfile(Model model, @PathVariable Optional<String> page) {
        return viewUserProfile(model, null, page);
    }

    @GetMapping(path = {"/users/{profileTag}", "/users/{profileTag}/{page}"})
    public String viewUserProfile(Model model, @PathVariable String profileTag, @PathVariable Optional<String> page) {
        SessionUser suser = accountService.getSessionUser();
        if (profileTag == null) {
            profileTag = suser.getAccount().getProfileTag();
        }
        System.out.println("viewUserProfile: profileTag: " + profileTag);
        System.out.println("PAGE: " + (page.isPresent() ? page.get() : "NOT AVAILABLE"));
        model.addAttribute("username", suser.getUsername());
        model.addAttribute("profileTag", "@test");
        model.addAttribute("urlname", profileTag);
        return "user-profile";
    }
}
