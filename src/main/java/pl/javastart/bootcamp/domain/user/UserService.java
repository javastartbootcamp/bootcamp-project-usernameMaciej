package pl.javastart.bootcamp.domain.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.signup.SignupService;
import pl.javastart.bootcamp.domain.user.role.Role;
import pl.javastart.bootcamp.domain.user.role.UserRole;
import pl.javastart.bootcamp.mail.MailService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static pl.javastart.bootcamp.domain.user.ActivationResult.*;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MailService mailService;
    private SignupService signupService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService, SignupService signupService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.signupService = signupService;
    }

    @Transactional
    public Optional<User> findByEmailWithAuthorities(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        byEmail.ifPresent(u -> u.getRoles().size());
        return byEmail;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User createAccount(String email, String phoneNumber, String firstName, String lastName,
                              String street, String houseNumber, String flatNumber, String postalCode, String city) {
        User user = new User();
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActivated(false);
        user.setCreatedDate(LocalDateTime.now());

        String address = street + " " + houseNumber;
        if (flatNumber != null && !flatNumber.isEmpty()) {
            address += "/" + flatNumber;
        }

        user.setAddress(address);
        user.setCity(city);
        user.setPostalCode(postalCode);
        user.setActivationCode(UUID.randomUUID().toString());

        UserRole userRole = new UserRole();
        userRole.setRole(Role.ROLE_USER);
        user.setRoles(Collections.singletonList(userRole));

        return userRepository.save(user);
    }

    public ActivationResult activateAccount(String code) {
        Optional<User> userOptional = userRepository.findByActivationCode(code);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getActivated()) {
                return ALREADY_ACTIVATED;
            }
            user.setActivated(true);
            String password = UUID.randomUUID().toString().substring(0, 8);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            mailService.sendAccountActivatedEmail(user.getEmail(), password);
            mailService.sendAccountActivatedEmailToAdmin(user);
            return OK;
        } else {
            return NOT_FOUND;
        }
    }

    public void deleteAccountByHimself(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(ResourceNotFoundException::new);
        deleteAccount(user);
        mailService.sendAccountDeletedByUserToAdminEmail(email);
    }

    public void deleteAccount(User user) {
        userRepository.delete(user);
    }

    public void changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(ResourceNotFoundException::new);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email).orElseThrow(ResourceNotFoundException::new);
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public Optional<User> findByAuthKey(String authKey) {
        return userRepository.findByAuthKey(authKey);
    }

    public void update(User user) {
        userRepository.save(user);
    }

    public void generateAuthKey(User user) {
        user.setAuthKey(UUID.randomUUID().toString());
        userRepository.save(user);
    }

    public List<User> findNotActivatedAccounts() {
        return userRepository.findByActivated(false);
    }

    public boolean sendPasswordResetLink(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPasswordResetKey(UUID.randomUUID().toString());
            userRepository.save(user);
            mailService.sendPasswordResetLink(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeUserPassword(String key, String password) {
        Optional<User> userOptional = userRepository.findByPasswordResetKey(key);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isCurrentUserAdmin() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean admin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return admin;
    }

    public User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return findByEmailOrThrow(authentication.getName());
    }

    @Transactional
    public void updateGithubUsername(String name, String githubUsername) {
        User user = findByEmailOrThrow(name);
        user.setGithubUsername(githubUsername);
    }
}
