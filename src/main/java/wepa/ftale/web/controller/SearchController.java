package wepa.ftale.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import wepa.ftale.service.UserService;

/**
 * @author Matias
 */
@Controller
public class SearchController {

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public String searchUsers(@RequestParam String name, Model model) {
        model.addAttribute("users", userService.findUsersWithSimilarName(name));
        userService.updateAuthenticatedUserToModel(model);
        return "search";
    }
}