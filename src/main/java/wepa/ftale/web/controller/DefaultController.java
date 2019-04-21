package wepa.ftale.web.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Matias
 */
@Controller
public class DefaultController {

    @GetMapping("/")
    public String helloWorld(Model model) {
        SecurityContext context = SecurityContextHolder.getContext();
        model.addAttribute("username", context.getAuthentication().getName());
        model.addAttribute("profileTag", "@test");
        return "index";
    }
}