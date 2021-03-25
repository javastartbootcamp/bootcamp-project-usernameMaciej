package pl.javastart.bootcamp.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.UserNotActivatedException;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;

import java.util.stream.Collectors;

@Service
public class CustomPreAuthUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Autowired
    private UserService userService;

    @Override
    public final UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) {

        String email = (String) token.getPrincipal();

        User user = userService.findByEmailWithAuthorities(email).orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found"));

        if (!user.getActivated()) {
            throw new UserNotActivatedException("User " + email + " was not activated");
        }

        org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole().name())).collect(Collectors.toList()));

        // user.setAuthKey(null);
        // userService.update(user);

        return springUser;
    }
}