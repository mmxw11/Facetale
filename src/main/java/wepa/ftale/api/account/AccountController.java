package wepa.ftale.api.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Matias
 */
@Controller
public class AccountController {

    @PostMapping("/api/account/sign-up")
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

    @PostMapping("/api/account/login")
    @ResponseBody
    public String handeLogin() {
        return "TODO";
    }
}