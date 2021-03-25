package pl.javastart.bootcamp.config.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.UserNotActivatedException;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;

import java.util.stream.Collectors;

@Service
public class DomainUserDetailsService implements UserDetailsService {

    private UserService userService;

    public DomainUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmailWithAuthorities(email).orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found"));

        if (!user.getActivated()) {
            throw new UserNotActivatedException("User " + email + " was not activated");
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole().name())).collect(Collectors.toList()));
    }

}
