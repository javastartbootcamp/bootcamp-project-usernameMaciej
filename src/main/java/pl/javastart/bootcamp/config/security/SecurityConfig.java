package pl.javastart.bootcamp.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.javastart.bootcamp.domain.user.UserService;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final int YEAR_IN_SECONDS = 60 * 60 * 24 * 30 * 365;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    @Qualifier("preAuthProvider")
    private AuthenticationProvider preAuthProvider;

    private DomainUserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private Environment environment;
    private UserService userService;

    @Autowired
    public SecurityConfig(DomainUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          Environment environment, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        auth.authenticationProvider(authenticationProvider).authenticationProvider(preAuthProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/konto/**").authenticated()
                .antMatchers("/api/phone-call").permitAll()
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            .and()
                .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login?error=true")
            .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
            .and()
                .rememberMe().key("fgg123ewdasf5132reghy4253rw")
                .tokenValiditySeconds(YEAR_IN_SECONDS)
            .and()
                .csrf()
                .ignoringAntMatchers("/h2-console/**")
                .ignoringAntMatchers("/api/**")
                .ignoringAntMatchers("/admin/img/upload")
        ;


        http.addFilterBefore(customAuthFilter(), BasicAuthenticationFilter.class);

        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            http.headers().frameOptions().disable();
        }
    }

    @Bean
    public LoginPerLinkFilter customAuthFilter() throws Exception {
        LoginPerLinkFilter filter = new LoginPerLinkFilter(userService);
        filter.setAuthenticationManager(authenticationManager());
        filter.setCheckForPrincipalChanges(true);
        return filter;
    }

}
