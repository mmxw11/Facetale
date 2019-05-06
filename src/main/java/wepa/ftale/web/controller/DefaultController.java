package wepa.ftale.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Matias
 */
@Controller
public class DefaultController {

    @GetMapping("/")
    public String helloWorld() {
        return "redirect:/me";
    }
}