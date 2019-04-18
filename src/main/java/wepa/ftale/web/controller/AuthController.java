package wepa.ftale.web.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Matias
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String handleLoginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH: " + auth + " | " + auth.isAuthenticated());
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "authentication/login";
    }

    @PostMapping("/api/auth/sign-up")
    @ResponseBody
    public String handleSignUp(@RequestParam String userId, @RequestParam String name,
            @RequestParam String profileTag, @RequestParam String password) {
        StringBuilder b = new StringBuilder();
        b.append("userId").append(": ").append(userId)
                .append(" name").append(": ").append(name)
                .append(" profileTag").append(": ").append(profileTag)
                .append(" password").append(": ").append(password);
        return b.toString();
    }
}