package wepa.ftale.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Matias
 */
@Profile("test")
@Configuration
@EnableWebSecurity
public class DevWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity sec) throws Exception {
        sec.authorizeRequests()
                // Resources.
                .antMatchers(HttpMethod.GET, "/static/**").permitAll()
                // Sign up.
                .antMatchers(HttpMethod.GET, "/sign-up").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/sign-up").permitAll()
                // Require login for everything else.
                .anyRequest().authenticated()
                .and()
                // Login.
                .formLogin().permitAll()
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
    /**
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder().passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
                .username("test")
                .password("moi")
                .roles("USER")
                .build();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(user);
        return manager;
    }*/
}