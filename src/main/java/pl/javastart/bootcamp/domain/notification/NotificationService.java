package pl.javastart.bootcamp.domain.notification;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupFacade;
import pl.javastart.bootcamp.domain.signup.SignupNotificationStatus;
import pl.javastart.bootcamp.mail.MailService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final int SECONDS_30 = 30000;

    private SignupFacade signupFacade;
    private MailService mailService;

    public NotificationService(SignupFacade signupFacade, MailService mailService) {
        this.signupFacade = signupFacade;
        this.mailService = mailService;
    }

    @Scheduled(fixedRate = SECONDS_30)
    public void sendNotifications() {
        handleNotSignedAgreementsAfter2days();
        handleNotSignedAgreementsAfter1day();
        handleNoPaymentAfter2Days();
        handleNoPaymentAfter1Day();
    }

    private void handleNotSignedAgreementsAfter1day() {
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        List<Signup> signups = signupFacade.findAllApproved();
        signups.stream()
                .filter(s -> s.getApprovedDate() != null && s.getApprovedDate().isBefore(dayAgo))
                .filter(s -> s.getNotificationStatus() == null)
                .forEach(s -> {
                    signupFacade.setNotificationStatus(s, SignupNotificationStatus.AGREEMENT_NOT_SIGNED_24);
                    mailService.sendNotSignedAgreementNotificationToAdmin(s, 1);
                });
    }

    private void handleNotSignedAgreementsAfter2days() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        List<Signup> signups = signupFacade.findAllApproved();
        signups.stream()
                .filter(s -> s.getApprovedDate() != null && s.getApprovedDate().isBefore(twoDaysAgo))
                .filter(s -> s.getNotificationStatus() == SignupNotificationStatus.AGREEMENT_NOT_SIGNED_24)
                .forEach(s -> {
                    signupFacade.setNotificationStatus(s, SignupNotificationStatus.AGREEMENT_NOT_SIGNED_48);
                    mailService.sendNotSignedAgreementNotificationToAdmin(s, 2);
                });
    }

    private void handleNoPaymentAfter1Day() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(1);
        List<Signup> signups = signupFacade.findAllWithAgreementSigned();
        signups.stream()
                .filter(s -> s.getApprovedDate() != null && s.getApprovedDate().isBefore(twoDaysAgo))
                .filter(s -> s.getNotificationStatus() == null)
                .forEach(s -> {
                    signupFacade.setNotificationStatus(s, SignupNotificationStatus.NO_PAYMENT_24);
                    mailService.sendNoPaymentNotificationToAdmin(s, 1);
                });
    }

    private void handleNoPaymentAfter2Days() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        List<Signup> signups = signupFacade.findAllWithAgreementSigned();
        signups.stream()
                .filter(s -> s.getApprovedDate() != null && s.getApprovedDate().isBefore(twoDaysAgo))
                .filter(s -> s.getNotificationStatus() == SignupNotificationStatus.NO_PAYMENT_24)
                .forEach(s -> {
                    signupFacade.setNotificationStatus(s, SignupNotificationStatus.NO_PAYMENT_48);
                    mailService.sendNoPaymentNotificationToAdmin(s, 2);
                });
    }

}

