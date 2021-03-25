package pl.javastart.bootcamp.domain.user;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.mail.MailService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserCleanupService {

    private static final int SECONDS_30 = 30000;

    private UserService userService;
    private MailService mailService;

    public UserCleanupService(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @Scheduled(fixedRate = SECONDS_30)
    @Transactional
    public void cleanupEmails() {
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        List<User> notActivatedAccounts = userService.findNotActivatedAccounts();
        notActivatedAccounts.stream()
                .filter(u -> u.getCreatedDate() != null && u.getCreatedDate().isBefore(dayAgo))
                .forEach(this::removeUser);
    }

    private void removeUser(User user) {
        userService.deleteAccount(user);
        mailService.sendAccountDeletedEmail(user.getEmail());
        mailService.sendAccountDeletedAutomaticallyToAdminEmail(user.getEmail());
    }
}
