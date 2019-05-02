package wepa.ftale.web.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import wepa.ftale.domain.UserSession;
import wepa.ftale.service.UserProfileService;

/**
 * @author Matias
 */
@Controller
public class UserProfileController {

    @Autowired
    private UserProfileService userService;

    @GetMapping(path = {"/me", "/me/{page}"})
    public String viewUserProfile(Model model, @PathVariable Optional<String> page) {
        return viewUserProfile(model, null, page);
    }

    @GetMapping(path = {"/users/{profileTag}", "/users/{profileTag}/{page}"})
    public String viewUserProfile(Model model, @PathVariable String profileTag, @PathVariable Optional<String> page) {
        UserSession userSession = userService.getUserSession();
        userService.updateUserSessionModel(model);
        System.out.println("viewUserProfile");
        System.out.println("PAGE: " + (page.isPresent() ? page.get() : "NOT AVAILABLE"));/*
        model.addAttribute("username", userSession.getUsername());*/
        model.addAttribute("urlname", "test");
        return "user-profile";
    }
}
