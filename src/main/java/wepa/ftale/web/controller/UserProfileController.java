package wepa.ftale.web.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Matias
 */
@Controller
public class UserProfileController {

    @GetMapping("/me")
    public String viewUserProfile(Model model) {
        SecurityContext context = SecurityContextHolder.getContext();
        model.addAttribute("username", context.getAuthentication().getName());
        model.addAttribute("profileTag", "@test");
        model.addAttribute("urlname", "me");
        return "user-profile";
    }

    @GetMapping("/users/{profileTag}")
    public String viewUserProfile(@PathVariable String profileTag, Model model) {
        SecurityContext context = SecurityContextHolder.getContext();
        model.addAttribute("username", context.getAuthentication().getName());
        model.addAttribute("profileTag", "@test");
        model.addAttribute("urlname", profileTag);
        return "user-profile";
    }
}
