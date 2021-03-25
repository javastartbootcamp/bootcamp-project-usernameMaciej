package pl.javastart.bootcamp.domain.signup;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.admin.signup.SignupAcceptDto;
import pl.javastart.bootcamp.domain.agreement.AgreementService;
import pl.javastart.bootcamp.domain.file.FileController;
import pl.javastart.bootcamp.domain.signup.log.SignupLogItemService;
import pl.javastart.bootcamp.domain.slack.SlackService;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;
import pl.javastart.bootcamp.mail.MailService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SignupFacade {

    private final TrainingService trainingService;
    private final SignupService signupService;
    private final SignupLogItemService signupLogItemService;
    private final UserService userService;
    private final MailService mailService;
    private final AgreementService agreementService;
    private final FileController fileController;
    private final SlackService slackService;

    public SignupFacade(TrainingService trainingService,
                        SignupService signupService,
                        SignupLogItemService signupLogItemService,
                        UserService userService,
                        MailService mailService,
                        AgreementService agreementService,
                        FileController fileController,
                        SlackService slackService) {
        this.trainingService = trainingService;
        this.signupService = signupService;
        this.signupLogItemService = signupLogItemService;
        this.userService = userService;
        this.mailService = mailService;
        this.agreementService = agreementService;
        this.fileController = fileController;
        this.slackService = slackService;
    }

    void processSignup(SignupDto signupDto) {
        Optional<User> userOptional = userService.findByEmail(signupDto.getEmail());
        User user = userOptional
                .orElseGet(() -> userService.createAccount(signupDto.getEmail(), signupDto.getPhoneNumber(),
                        signupDto.getFirstName(), signupDto.getLastName(),
                signupDto.getStreet(), signupDto.getHouseNumber(), signupDto.getFlatNumber(), signupDto.getPostalCode(), signupDto.getCity()));

        Training training = trainingService.findById(signupDto.getTrainingId()).orElseThrow(() -> new IllegalStateException("Training not found"));

        Signup signup = signupService.createSignup(user, training, signupDto.getMessage(), signupDto.getFinancingMethod());
        signupLogItemService.addNote(signup, "Zarejestrowano zgłoszenie");

        if (user.getActivated()) {
            mailService.sendAccountActivatedEmailToAdmin(user);
        } else {
            mailService.sendSignupEmail(signupDto.getEmail(), training.getDescription().getTitle(), user.getActivationCode());
            mailService.sendSignupEmailToAdmin(signup);
        }
    }

    public void updateDataAndAccept(Long id, SignupAcceptDto signupAcceptDto) {
        Signup signup = signupService.findById(id).orElseThrow(ResourceNotFoundException::new);
        signup.setApprovedDate(LocalDateTime.now());
        if (!signupAcceptDto.isAdvancePaymentToDefault()) {
            signup.setAdvancePaymentToOverride(signupAcceptDto.getAdvancePaymentTo());
        }
        if (!signupAcceptDto.isFullPaymentFromDefault()) {
            signup.setFullPaymentFromOverride(signupAcceptDto.getFullPaymentFrom());
        }
        if (!signupAcceptDto.isFullPaymentToDefault()) {
            signup.setFullPaymentToOverride(signupAcceptDto.getFullPaymentTo());
        }
        signup.setDeposit(signupAcceptDto.getDeposit());
        signup.setPrice(signupAcceptDto.getPrice());
        signup.setCustomPaymentInfo(signupAcceptDto.getCustomPaymentInfo());
        calculateAdvancePaymentTo(signup, LocalDate.now());
        signupService.update(signup);

        User user = signup.getUser();
        user.setFirstName(signupAcceptDto.getFirstName());
        user.setLastName(signupAcceptDto.getLastName());
        user.setPhoneNumber(signupAcceptDto.getPhoneNumber());
        user.setEmail(signupAcceptDto.getEmail());
        user.setAddress(signupAcceptDto.getAddress());
        user.setPostalCode(signupAcceptDto.getPostalCode());
        user.setCity(signupAcceptDto.getCity());
        userService.update(user);

        signupLogItemService.addNote(signup, "Zaakceptowano zapis");

        Training training = signup.getTraining();
        List<Signup> signups = training.getSignups().stream()
                .filter(s -> s.getStatus() != SignupStatus.REJECTED)
                .sorted(Comparator.comparing(Signup::getId))
                .collect(Collectors.toList());
        int userListPosition = signups.indexOf(signup) + 1;
        int totalSignupsCount = signups.size();

        if (totalSignupsCount <= training.getMaxAttendees()) {
            String filename = agreementService.prepareTemplateAgreementForSignup(signup);
            signup.setAgreementTemplateFileName(filename);
            signup.setStatus(SignupStatus.APPROVED);
            signupService.update(signup);
            mailService.sendAcceptedEmail(signup);
        } else {
            int userPlace = userListPosition - training.getMaxAttendees();
            signup.setStatus(SignupStatus.RESERVE);
            signupService.update(signup);
            handleWaitingQueueAfterSignup(signup, userPlace);
        }
    }

    private void handleWaitingQueueAfterSignup(Signup signup, int currentPlace) {
        mailService.sendAcceptedButOnWaitingListEmail(signup, currentPlace);
    }

    public void calculateAdvancePaymentTo(Signup signup, LocalDate today) {
        signup.setAdvancePaymentTo(addWorkingDays(today, 2));
    }

    public void generateAgreementSync(Long id) {
        generateAgreementAsync(id);
    }

    @Async
    public void generateAgreementAsync(Long id) {
        Signup signup = signupService.findById(id).orElseThrow(ResourceNotFoundException::new);
        String filename = agreementService.prepareTemplateAgreementForSignup(signup);
        signup.setAgreementTemplateFileName(filename);
        signupService.update(signup);
        signupLogItemService.addNote(signup, "Ponownie wygenerowano wzór umowy");
        String previousFilename = signup.getAgreementTemplateFileName();
        fileController.removeAgreementFile(previousFilename);
    }

    public void advancePaid(Long id) {
        Signup signup = signupService.findById(id).orElseThrow(ResourceNotFoundException::new);
        signup.setStatus(SignupStatus.ADVANCE_PAID);
        signupService.update(signup);
        signupLogItemService.addNote(signup, "Odnotowano opłacenie zadatku");
        mailService.sendAdvancePaidEmail(signup);
    }

    public void fullyPaid(Long id) {
        Signup signup = signupService.findById(id).orElseThrow(ResourceNotFoundException::new);
        signup.setStatus(SignupStatus.PAID);
        signup.setCanSeeContent(true);
        signupService.update(signup);
        signupLogItemService.addNote(signup, "Odnotowano opłacenie szkolenia w całości");
        mailService.sendFullyPaidEmail(signup);
    }

    public void rejectSignupByUser(Long signupId) {
        rejectSignup(signupId, "Użytkownik zrezygnował ze szkolenia.");
        Signup signup = signupService.findById(signupId).orElseThrow(ResourceNotFoundException::new);
        mailService.sendUserResignedFromTraining(signup);
    }

    public void rejectSignup(Long signupId, String reason) {
        Signup signup = signupService.findById(signupId).orElseThrow(ResourceNotFoundException::new);
        handleWaitingQueueAfterReject(signup);
        signup.setStatus(SignupStatus.REJECTED);
        signup.setCanSeeContent(false);
        signupService.update(signup);
        signupLogItemService.addNote(signup, "Zapis odrzucony. Powód: " + reason);
        mailService.sendRejectedEmail(signup, reason);
    }

    private void handleWaitingQueueAfterReject(Signup signup) {
        Training training = signup.getTraining();
        List<Signup> signups = training.getSignups().stream()
                .filter(s -> s.getStatus() != SignupStatus.REJECTED)
                .sorted(Comparator.comparing(Signup::getId))
                .collect(Collectors.toList());
        int userListPosition = signups.indexOf(signup) + 1;
        int totalSignupsCount = signups.size();

        if (userListPosition == totalSignupsCount) {
            // rejected user was last - nobody to move
            return;
        }

        if (userListPosition <= training.getMaxAttendees()) {
            // a place was freed
            if (signups.size() > training.getMaxAttendees()) {
                Signup freedSignup = signups.get(training.getMaxAttendees());
                if (freedSignup.getStatus() != SignupStatus.NEW) {
                    freedSignup.setStatus(SignupStatus.APPROVED);
                    freedSignup.setApprovedDate(LocalDateTime.now());
                    calculateAdvancePaymentTo(freedSignup, LocalDate.now());
                    String filename = agreementService.prepareTemplateAgreementForSignup(freedSignup);
                    freedSignup.setAgreementTemplateFileName(filename);
                    signupService.update(freedSignup);
                    userService.generateAuthKey(freedSignup.getUser());
                    mailService.sendPlaceFreeMail(freedSignup);
                    signupLogItemService.addNote(freedSignup, "Wysłano maila, że zwolniło się miejsce na szkolenie");
                }
            }
        }

        int startAt = Math.max(training.getMaxAttendees() + 2, userListPosition + 1);

        for (int i = startAt; i <= totalSignupsCount; i++) {
            Signup s = signups.get(i - 1);
            if (s.getStatus() != SignupStatus.NEW) {
                userService.generateAuthKey(s.getUser());
                int place = i - training.getMaxAttendees() - 1;
                mailService.sendReservePlaceChangedEmail(s, place);
                signupLogItemService.addNote(s, "Wysłano maila, że jest " + place + " na liście rezerwowych");
            }
        }
    }

    public void addNote(Long id, String text) {
        Signup signup = signupService.findById(id).orElseThrow(ResourceNotFoundException::new);
        signupLogItemService.addNote(signup, text);
    }

    public void sendMessage(Long id, String title, String message) {
        Signup signup = signupService.findById(id).orElseThrow(ResourceNotFoundException::new);
        mailService.sendMessageToUser(signup, title, message);
        signupLogItemService.addNote(signup, "Wysłano wiadomość: " + title + " | Treść: " + message);
    }

    public List<Signup> findByUserEmail(String email) {
        User user = userService.findByEmailOrThrow(email);
        return signupService.findByUser(user);
    }

    public List<Signup> findWithAccessByUserEmail(String email) {
        User user = userService.findByEmailOrThrow(email);
        return signupService.findByUserAndWithAccess(user);
    }

    public Signup findByIdOrThrow(Long id) {
        return signupService.findByIdOrThrow(id);
    }

    @Async
    public void processAgreementSignedAsync(Long signupId, String remoteAddr) {
        Signup signup = signupService.findByIdOrThrow(signupId);
        signup.setStatus(SignupStatus.AGREEMENT_SIGNED);
        signup.setAgreementSigningIp(remoteAddr);
        signup.setAgreementSigningDate(LocalDateTime.now());

        String filename = agreementService.prepareSignedAgreementForSignup(signup, LocalDate.now());
        signup.setAgreementSignedFileName(filename);
        signupService.update(signup);

        signupLogItemService.addNote(signup, "Umowa podpisania z urządzenia o IP: " + remoteAddr);
        mailService.sendAgreementSignedEmail(signup);
        mailService.sendAgreementSignedEmailToAdmin(signup);
    }

    public String getAgreementTemplateFileNameForSignupId(Long id) {
        return signupService.findByIdOrThrow(id).getAgreementTemplateFileName();
    }

    public List<Signup> findAllApproved() {
        return signupService.findByStatus(SignupStatus.APPROVED);
    }

    public List<Signup> findAllWithAgreementSigned() {
        return signupService.findByStatus(SignupStatus.AGREEMENT_SIGNED);
    }

    public void setNotificationStatus(Signup signup, SignupNotificationStatus status) {
        signup.setNotificationStatus(status);
        signupService.update(signup);
    }

    private LocalDate addWorkingDays(LocalDate localDate, int count) {
        int added = 0;
        LocalDate resultDate = LocalDate.from(localDate);
        while (added < count) {
            resultDate = resultDate.plusDays(1);
            if (resultDate.getDayOfWeek() != DayOfWeek.SATURDAY && resultDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                added++;
            }
        }
        return resultDate;
    }

    public void registerAgreementSiteVisit(Long id) {
        Signup signup = signupService.findByIdOrThrow(id);
        signupLogItemService.addNote(signup, "Strona z umową została odwiedzona przez kursanta");
    }

    public void updateAccess(Long id, Boolean access) {
        Signup signup = signupService.findByIdOrThrow(id);
        signup.setCanSeeContent(access);
        signupService.update(signup);
    }

    public void updateSignupSlackChannel(Signup signup) {
        Signup signupToUpdate = signupService.findById(signup.getId()).orElseThrow();
        signupToUpdate.setSlackChannelId(signup.getSlackChannelId());
        String slackBotAccessToken = signupToUpdate.getTraining().getSlackBotAccessToken();
        slackService.sendSlackNotification("Udało się podpiąć bota.", signup.getSlackChannelId(), slackBotAccessToken);
        signupService.update(signupToUpdate);
    }
}
