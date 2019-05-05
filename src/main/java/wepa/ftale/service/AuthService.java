package wepa.ftale.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import wepa.ftale.domain.Account;
import wepa.ftale.domain.FtImage;
import wepa.ftale.domain.Post;
import wepa.ftale.repository.AccountRepository;
import wepa.ftale.repository.ImageRepository;
import wepa.ftale.repository.PostRepository;

/**
 * @author Matias
 */
@Service
public class AuthService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private PostRepository postRepository;

    @PostConstruct
    public void addAccount() throws IOException {
        Account account2 = new Account("jorma", "jorma Ojala", "jormala", passwordEncoder.encode("testi"), null);
        Account account = new Account("testi", "testiFullName", "testTag", passwordEncoder.encode("testi"), null);
        accountRepository.save(account);
        accountRepository.save(account2);
        List<Post> posts = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            Post post = new Post();
            post.setAuthor(r.nextBoolean() ? account2 : account);
            post.setTarget(account);
            post.setTextContent(i
                    + ". Lorem Ipsum is simply dummy text of the printing and typesetting industry. "
                    + "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, "
                    + "when an unknown printer took a galley of type and scrambled it to make a type "
                    + "specimen book. It has survived not only five centuries, but also the leap into "
                    + "electronic typesetting, remaining essentially unchanged. It was popularised in the "
                    + "1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more "
                    + "recently with desktop publishing software like Aldus PageMaker including versions of "
                    + "Lorem Ipsum.");
            posts.add(post);
        }
        postRepository.saveAll(posts);
        System.out.println("UUID: " + account.getId());
        byte[] bytes = Files.readAllBytes(
                Paths.get("src/main/resources/static/images/export.png"));
        FtImage image = new FtImage(account, (long) bytes.length, "image/png", bytes);
        imageRepository.save(image);
    }

    @Transactional
    public void createAccount(Account account, BindingResult bindingResult) {
        List<Account> accounts = accountRepository.findAllByUsernameOrProfileTagAllIgnoreCase(account.getUsername(), account.getProfileTag());
        if (!accounts.isEmpty()) {
            if (accounts.size() > 2) {
                // This should be impossible to happen. If there are more than two items returned
                // that means we have multiple accounts with the same profile tag or username,
                // which should have never happened.
                throw new RuntimeException("Duplicated accounts error! Found multiple accounts with the same username or profile tag!");
            }
            // There's already an account registered with the username or profile tag.
            createUnableToCreateAccountResult(account, accounts.get(0), bindingResult);
            if (accounts.size() > 1) {
                createUnableToCreateAccountResult(account, accounts.get(1), bindingResult);
            }
            return;
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
    }

    private void createUnableToCreateAccountResult(Account account, Account facc, BindingResult bindingResult) {
        if (facc.getProfileTag().equalsIgnoreCase(account.getProfileTag())) {
            bindingResult.addError(new FieldError("account", "profileTag", "This profile tag is already used by another user."));
        }
        if (facc.getUsername().equalsIgnoreCase(account.getUsername())) {
            bindingResult.addError(new FieldError("account", "username", "This username is already used by another user."));
        }
    }
}
