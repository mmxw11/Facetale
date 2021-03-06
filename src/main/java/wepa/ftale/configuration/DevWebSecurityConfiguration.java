package wepa.ftale.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import wepa.ftale.domain.Account;
import wepa.ftale.repository.AccountRepository;

/**
 * @author Matias
 */
@Profile("test")
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableWebSecurity
public class DevWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    */
    
    @PostConstruct
    private void createSampleData() {
        // Account.
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Account acc = new Account("accusername-" + i, "accname-" + i, "accprofiletag-" + i, passwordEncoder.encode("testi"), null);
            accounts.add(accountRepository.save(acc));
        }
        // Posts.
        /**
          Random r = new Random(); 
          for (int i = 0; i < 1 + r.nextInt(50); i++) {
            Post post = new Post(accounts.get(r.nextInt(accounts.size())), accounts.get(r.nextInt(accounts.size())),
                    LocalDateTime.now(), "test-viesti-" + i, null, new ArrayList<>(), new ArrayList<>());
            post = postRepository.save(post);
            for (int il = 0; il < r.nextInt(10); il++) {
                post.getLikes().add(accounts.get(r.nextInt(accounts.size())));
            }
            post = postRepository.save(post);
            for (int il = 0; il < r.nextInt(20); il++) {
                Comment comment = new Comment(accounts.get(r.nextInt(accounts.size())), post, LocalDateTime.now(),
                        "test-kommentti-" + i);
                commentRepository.save(comment);
            }
        }*/
    }

    @Override
    protected void configure(HttpSecurity sec) throws Exception {
        // H2-console.
        sec.csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().sameOrigin();
        sec.authorizeRequests()
                // H2-console
                .antMatchers("/h2-console", "/h2-console/**").permitAll()
                // Resources.
                .antMatchers(HttpMethod.GET, "/static/**").permitAll()
                // Sign up.
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/sign-up").permitAll()
                // Only uses who are NOT authenticated can access the processing endpoints.
                .antMatchers(HttpMethod.POST, "/api/auth/sign-up").hasRole("ANONYMOUS")
                .antMatchers(HttpMethod.POST, "/api/auth/login").hasRole("ANONYMOUS")
                // Require login for everything else.
                .anyRequest().authenticated()
                .and()
                // Login.
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?fail=true")
                .loginProcessingUrl("/api/auth/login")
                .defaultSuccessUrl("/", true)
                // Logout.
                .and()
                .logout().permitAll()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}