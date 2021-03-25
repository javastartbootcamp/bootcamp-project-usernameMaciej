package pl.javastart.bootcamp.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class LoginPerLinkFilter extends AbstractPreAuthenticatedProcessingFilter {

    private UserService userService;

    public LoginPerLinkFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        Object currentUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            currentUser = authentication.getPrincipal();
        }
        String authKey = httpServletRequest.getParameter("authKey");
        if (authKey != null) {
            Optional<User> byAuthKey = userService.findByAuthKey(authKey);
            return byAuthKey.map(user -> (Object) user.getEmail()).orElse(currentUser);
        }
        return currentUser;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return "";
    }

}
