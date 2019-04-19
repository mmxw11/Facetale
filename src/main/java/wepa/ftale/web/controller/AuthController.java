package wepa.ftale.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import wepa.ftale.db.repository.AccountRepository;
import wepa.ftale.domain.Account;

/**
 * @author Matias
 */
@Controller
public class AuthController {
    
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String handleLoginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("LOGIN AUTH: " + auth + " | " + auth.isAuthenticated());
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/sign-up")
    public String handleSignUpPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("SIGN UP PAGE AUTH: " + auth + " | " + auth.isAuthenticated());
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "auth/sign-up";
    }

    @PostMapping("/api/auth/sign-up")
    public String handleSignUp(@Valid @ModelAttribute Account account, BindingResult bindingResult) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        System.out.println("AUTH: SIGN PAGE " + auth + " | " + auth.isAuthenticated());
        System.out.println("new acc: " + account);
        //TODO: Do something with the unique fields (USERNAME AND PRORIFLETAG)
        if (bindingResult.hasErrors()) {
            /*bindingResult.addError(new FieldError("account", "profileTag", "TEST ERROR: " + account.getProfileTag()));
            */
            System.out.println("errors!");
            return "auth/sign-up";
        }
        System.out.println("acc suc!");
        accountRepository.save(account);
        // We prob want to log in the user here.
        return "redirect:/";
    }

    @ModelAttribute
    private Account getAccount() {
        return new Account();
    }
}