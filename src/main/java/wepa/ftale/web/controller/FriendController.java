package wepa.ftale.web.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import wepa.ftale.domain.Account;
import wepa.ftale.service.UserService;

/**
 * @author Matias
 */
@Controller
public class FriendController {

    @Autowired
    private UserService userService;

    @GetMapping("/friends")
    public String getFriends(@RequestParam String user, Model model) {
        model.addAttribute("user", userService.getAuthenticatedUserAccount());
        userService.updateAuthenticatedUserToModel(model);
        userService.buildFriendsView(user, model);
        return "friends";
    }

    @PostMapping("/api/user/sendfriendreq")
    @PreAuthorize("authentication.principal.id == #senderId")
    public String handleSendFriendRequest(@RequestParam UUID senderId, @RequestParam UUID recipientId) {
        Account recipient = userService.sendFriendRequest(senderId, recipientId);
        return "redirect:/users/" + recipient.getProfileTag();
    }

    @PostMapping("/api/user/friendreq")
    @PreAuthorize("authentication.principal.id == #accountId")
    public String handleFriendRequest(@RequestParam UUID accountId, @RequestParam String action, @RequestParam long friendRequestId) {
        if (!action.equals("ACCEPT") && !action.equals("DENY") && !action.equals("CANCEL")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown action!");
        }
        Account account = userService.handleFriendRequest(accountId, action, friendRequestId);
        return "redirect:/friends?user=" + account.getProfileTag();
    }
}