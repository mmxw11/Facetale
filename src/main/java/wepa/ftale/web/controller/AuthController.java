package wepa.ftale.web.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import wepa.ftale.domain.Account;
import wepa.ftale.service.AuthService;
import wepa.ftale.service.UserService;
import wepa.ftale.web.FormUtils;

/**
 * @author Matias
 */
@Controller
public class AuthController {

    @Autowired
    private AuthService accountService;
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String viewLoginPage() {
        if (userService.isUserAuthenticated()) {
            // Redirect the user if they are already authenticated.
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/sign-up")
    public String viewSignUpPage(Model model) {
        if (userService.isUserAuthenticated()) {
            // Redirect the user if they are already authenticated.
            return "redirect:/";
        }
        if (!model.containsAttribute("account")) {
            model.addAttribute("account", new Account());
        }
        return "auth/sign-up";
    }

    @PostMapping("/api/auth/login")
    public void handleSignUp(@RequestParam String username, @RequestParam String password, HttpServletRequest request) throws ServletException {
        // This controller ensures that user can't post anything to the login processing endpoint
        // when they are already logged in. (Needed for HttpSecurity to work)
        request.login(username, password);
    }

    @PostMapping("/api/auth/sign-up")
    public String handleSignUp(@Valid @ModelAttribute Account account, BindingResult bindingResult, RedirectAttributes rdAttributes,
            HttpServletRequest request) throws ServletException {
        // Form validation errors.
        if (FormUtils.redirectBindingResult("account", account, bindingResult, rdAttributes)) {
            return "redirect:/sign-up";
        }
        final String originalPassword = account.getPassword();
        account = accountService.createAccount(account, bindingResult);
        if (FormUtils.redirectBindingResult("account", account, bindingResult, rdAttributes)) {
            // createAccount failed.
            return "redirect:/sign-up";
        }
        // Sign up succeeded. Login the user automatically.
        request.login(account.getUsername(), originalPassword);
        return "redirect:/";
    }
}