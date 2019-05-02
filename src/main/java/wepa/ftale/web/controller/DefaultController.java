package wepa.ftale.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import wepa.ftale.service.UserService;

/**
 * @author Matias
 */
@Controller
public class DefaultController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String helloWorld(Model model) {
        /* TODO: REDIRECT USER TO PROFILE PAGE? */
        userService.updateAuthenticatedUserToModel(model);
        return "index";
    }
}