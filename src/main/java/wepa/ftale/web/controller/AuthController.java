package wepa.ftale.web.controller;

import javax.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import wepa.ftale.domain.Account;

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
        return "auth/login";
    }

    @GetMapping("/sign-up")
    public String handleSignUpPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH: " + auth + " | " + auth.isAuthenticated());
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "auth/sign-up";
    }

    @PostMapping("/api/auth/sign-up")
    public String handleSignUp(@Valid @ModelAttribute Account account, BindingResult bindingResult) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH: " + auth + " | " + auth.isAuthenticated());
        System.out.println("new acc: " + account);
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new FieldError("account", "profileTag", "TEST ERROR: " + account.getProfileTag()));
            System.out.println("errors!");
            return "auth/sign-up";
        }
        System.out.println("acc suc!");
        return "redirect:/";
    }

    @ModelAttribute
    private Account getAccount() {
        return new Account();
    }
}