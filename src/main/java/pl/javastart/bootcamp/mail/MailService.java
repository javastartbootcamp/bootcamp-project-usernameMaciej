package pl.javastart.bootcamp.mail;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.javastart.bootcamp.config.JavaStartProperties;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.user.User;

import java.io.File;

@Service
public class MailService {

    private static final String ADMIN_EMAIL = "javastarttester@gmail.com";

    private final AsyncMailSender asyncMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final JavaStartProperties javaStartProperties;

    public MailService(AsyncMailSender asyncMailSender, SpringTemplateEngine springTemplateEngine, JavaStartProperties javaStartProperties) {
        this.asyncMailSender = asyncMailSender;
        this.springTemplateEngine = springTemplateEngine;
        this.javaStartProperties = javaStartProperties;
    }

    public void sendSignupEmail(String to, String trainingTitle, String activationCode) {
        String title = "Wymagane potwierdzenie maila do zapisu na " + trainingTitle;
        String message = "Dziękujemy za zapis na " + trainingTitle + ". Potwierdź proszę ten adres email klikając w link poniżej.";
        String buttonText = "Potwierdzam email";
        String buttonUrl = "/potwierdzKonto?code=" + activationCode;
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(to, title, content);
    }

    public void sendSignupEmailToAdmin(Signup signup) {
        String code = signup.getTraining().getCode();
        User user = signup.getUser();
        String title = "Zapis na szkolenie " + code;
        String message = user.getFirstName() + " " + user.getLastName() + " zapisał(a) się na szkolenie " + code + ". Czekamy aż potwierdzi maila.<br>";
        message += "Forma finansowania: " + signup.getFinancingMethod() + "<br>";
        message += "Wiadomość podczas zapisu: " + signup.getNote();
        String buttonText = "Zobacz profil";
        String buttonUrl = "/admin/uzytkownicy/" + signup.getUser().getId();
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, content);
    }

    public void sendAccountActivatedEmail(String to, String password) {
        String title = "Konto aktywowane i hasło do konta";
        String message = "Twoje konto zostało aktywowane. Możesz zalogować się za pomocą hasła: <b>" + password + "</b>. Sugerujemy jego zmianę.<br>"
                + "Sprawdzamy Twoje zgłoszenie. Jeśli wszystko pójdzie dobrze to niebawem podeślemy maila z dalszymi instrukcjami.";
        String buttonText = "Logowanie";
        String buttonUrl = "/login";
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(to, title, content);
    }

    public void sendAccountDeletedEmail(String email) {
        String title = "Konto usunięte";
        String message = "Z powodu braku potwierdzenia adresu email Twoje konto i zapis na szkolenie zostały usunięte. "
                + "Jeśli chcesz to możesz zapisać się ponownie.";
        String content = prepareContentFromBaseLayout(title, message, null, null);
        asyncMailSender.sendEmail(email, title, content);
    }

    public void sendAccountActivatedEmailToAdmin(User user) {
        String message = "Sprawdź poprawność danych:<br><br>";
        message += user.getFirstName() + " " + user.getLastName() + "<br>";
        message += "Email: " + user.getEmail() + "<br>";
        message += "Telefon: " + user.getPhoneNumber() + "<br>";
        message += "Adres: " + user.getAddress() + "<br>";
        message += "Adres: " + user.getPostalCode() + " " + user.getCity() + "<br>";
        String buttonText = "Przejdź do zatwierdzania";
        String buttonUrl = "/admin/uzytkownicy/" + user.getId();
        String title = "[Wymagane działanie] Sprawdź dane kursanta " + user.getFirstName() + " " + user.getLastName();
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, content);
    }

    private String prepareContentFromBaseLayout(String title, String message, String buttonText, String buttonUrl) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("introText", message);
        context.setVariable("actionButtonText", buttonText);
        context.setVariable("actionButtonLink", javaStartProperties.getFullDomainAddress() + buttonUrl);
        return springTemplateEngine.process("mails/baseMailLayout", context);
    }

    public void sendAccountDeletedByUserToAdminEmail(String email) {
        String title = "Użytkownik usunął konto";
        String message = "Użytkownik " + email + " usunął konto.";
        String content = prepareContentFromBaseLayout(title, message, null, null);
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, content);
    }

    public void sendAccountDeletedAutomaticallyToAdminEmail(String email) {
        String title = "Konto użytkownika zostało usunięte";
        String message = "Konto użytkownika " + email + " zostało automatycznie usunięte z powodu braku aktywacji w ciągu 24h.";
        String content = prepareContentFromBaseLayout(title, message, null, null);
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, content);
    }

    public void sendAcceptedEmail(Signup signup) {
        User user = signup.getUser();
        String title = "[Wymagane działanie] Akceptacja umowy";
        String message = "Sprawdziliśmy Twoje dane i wszystko wygląda w porządku. "
                + "Aby dokończyć rezerwację prosimy o potwierdzenie zapoznania się z umową poprzez poniższy link.";
        String buttonText = "Przejdź do podpisania umowy";
        String buttonUrl = "/konto/zapisy/" + signup.getId() + "/umowa";
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(user.getEmail(), title, content);
    }

    public void sendAcceptedButOnWaitingListEmail(Signup signup, int currentPlace) {
        User user = signup.getUser();

        String title = "Zapis zaakceptowany. Lista rezerwowych.";
        String message = "Sprawdziliśmy Twoje dane i wszystko wygląda w porządku. Aktualnie jesteś na " + currentPlace + " miejscu na liście rezerwowych. "
                + "Będziemy na bieżąco informowali o zmianach. ";

        String buttonText = "Status szkolenia";
        String buttonUrl = "/konto/zapisy/" + signup.getId() + "?authKey=" + user.getAuthKey();

        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(user.getEmail(), title, content);
    }

    public void sendAdvancePaidEmail(Signup signup) {
        User user = signup.getUser();
        String title = "Otrzymaliśmy zadatek";
        String trainingTitle = signup.getTraining().getDescription().getTitle();
        String message = "Potwierdzamy otrzymanie zadatku za szkolenie " + trainingTitle + ". Pozostałą kwotę zaczniemy zbierać 2 tygodnie przed szkoleniem.";
        String buttonText = "Zobacz zapisy";
        String buttonUrl = "/konto/zapisy";
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(user.getEmail(), title, content);
    }

    public void sendFullyPaidEmail(Signup signup) {
        User user = signup.getUser();
        String title = "Otrzymaliśmy zapłatę za szkolenie";
        String trainingTitle = signup.getTraining().getDescription().getTitle();
        String message = "Potwierdzamy otrzymanie płatności za szkolenie " + trainingTitle + ".";
        String buttonText = "Zobacz zapisy";
        String buttonUrl = "/konto/zapisy";
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(user.getEmail(), title, content);
    }

    public void sendRejectedEmail(Signup signup, String reason) {
        User user = signup.getUser();
        String title = "Zapis na szkolenie odrzucony";
        String trainingTitle = signup.getTraining().getDescription().getTitle();
        String message = "Twój zapis na szkolenie " + trainingTitle + " został odrzucony. Powód odrzucenia: " + reason;
        String content = prepareContentFromBaseLayout(title, message, null, null);
        asyncMailSender.sendEmail(user.getEmail(), title, content);
    }

    public void sendAgreementSignedEmail(Signup signup) {
        String agreementsDir = javaStartProperties.getAgreementsDir();
        File attachment = new File(agreementsDir, signup.getAgreementSignedFileName());

        User user = signup.getUser();
        String title = "Potwierdzona umowa";
        String trainingTitle = signup.getTraining().getDescription().getTitle();
        String message = "Dziękujemy za zaakceptowanie umowy do szkolenia " + trainingTitle + ". Umowa jest załączona do tego maila.";
        String buttonText = "Zobacz dane do wpłaty";
        String buttonUrl = "/konto/zapisy/" + signup.getId();
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmailWithAttachment(user.getEmail(), title, content, "umowa.pdf", attachment);
    }

    public void sendAgreementSignedEmailToAdmin(Signup signup) {
        User user = signup.getUser();
        String title = "Umowa podpisana";
        String message = user.getFirstName() + " " + user.getLastName() + " podpisał(a) umowę.";
        String content = prepareContentFromBaseLayout(title, message, null, null);
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, content);
    }

    public void sendPlaceFreeMail(Signup signup) {
        User user = signup.getUser();
        String url = javaStartProperties.getFullDomainAddress() + "/konto/zapisy/" + signup.getId() + "?authKey=" + user.getAuthKey();

        String trainingTitle = signup.getTraining().getDescription().getTitle();
        String title = "Wolne miejsce na " + trainingTitle;
        String message = "Właśnie zwolniło się miejsce na " + trainingTitle + ". W celu potwiedzenia uczestnictwa w "
                + "szkoleniu prosimy o potwierdzenie zapoznania się z umową poprzez poniższy link.<br>";
        message += "Na potwierdzenie masz 24 godziny. Jeśli chcesz zrezygnować możesz to zrobić ";
        message += "<a href=\"" + url + "\">tutaj</a>.";

        String buttonText = "Przejdź do podpisania umowy";
        String buttonUrl = "/konto/zapisy/" + signup.getId() + "/umowa";
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);

        asyncMailSender.sendEmail(signup.getUser().getEmail(), title, content);
    }

    public void sendReservePlaceChangedEmail(Signup signup, int place) {
        User user = signup.getUser();

        String title = "Zmiana miejsca na liście rezerwowych";
        String message = "Ktoś właśnie zrezygnował ze szkolenia. Aktualnie jesteś " + place + " na liście rezerwowej. "
                + "Będziemy na bieżąco informowali o zmianach. ";

        String buttonText = "Status szkolenia";
        String buttonUrl = "/konto/zapisy/" + signup.getId() + "?authKey=" + user.getAuthKey();
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);

        asyncMailSender.sendEmail(signup.getUser().getEmail(), title, content);
    }

    public void sendNotSignedAgreementNotificationToAdmin(Signup signup, int days) {
        String dayOrDaysString = (days == 1) ? "dnia" : "dni";
        User user = signup.getUser();
        String title = "Kursant nie podpisał umowy w ciągu " + days + " " + dayOrDaysString;
        String message = "Kursant " + user.getEmail() + " nie podpisał umowy w ciągu " + days + " " + dayOrDaysString + " od jej otrzymania.<br>";
        message += "Przydałoby się upomnieć o podpisanie umowy, albo odrzucić zapis";
        String buttonText = "Przejdź do zarządzania zapisem";
        String buttonUrl = "/admin/zapisy/" + signup.getId();
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, content);
    }

    public void sendNoPaymentNotificationToAdmin(Signup signup, int days) {
        String dayOrDaysString = (days == 1) ? "dnia" : "dni";
        User user = signup.getUser();
        String title = "Kursant nie wpłacił zaliczki w ciągu " + days + " " + dayOrDaysString;
        String message = "Kursant " + user.getEmail() + " nie wpłacił zaliczki w ciągu " + days + " " + dayOrDaysString + " od jej otrzymania.<br>";
        message += "Przydałoby się sprawdzić czy kasa doszła, upomnieć o wpłatę, albo odrzucić zapis";
        String buttonText = "Przejdź do zarządzania zapisem";
        String buttonUrl = "/admin/zapisy/" + signup.getId();
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, content);
    }

    public void sendMessageToUser(Signup signup, String title, String message) {
        String buttonText = "Przejdź do zapisu na szkolenie";
        String buttonUrl = "/konto/zapisy/" + signup.getId();
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(signup.getUser().getEmail(), title, content);
    }

    public void sendUserResignedFromTraining(Signup signup) {
        User user = signup.getUser();
        String title = "Użytkownik zrezygnował ze szkolenia";
        String message = "Użytkownik " + user.getEmail() + " zrezygnował ze szkolenia.<br>";
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, message);
    }

    public void sendPasswordResetLink(User user) {
        String title = "Resetowanie hasła";
        String message = "Skorzystaj z linku poniżej w celu zresetowania hasła.";
        String buttonText = "Resetuj hasło";
        String buttonUrl = "/reset-hasla?key=" + user.getPasswordResetKey();
        String content = prepareContentFromBaseLayout(title, message, buttonText, buttonUrl);
        asyncMailSender.sendEmail(user.getEmail(), title, content);
    }

    public void sendPhoneRequestMail(String firstName, String phoneNumber, String contactDate) {
        String message = "Użytkownik poprosił o kontakt telefoniczny<br>";
        message += "Imię: " + firstName + "<br>";
        message += "Numer telefonu: " + phoneNumber + "<br>";
        message += "Kiedy zadzwonić: " + contactDate;
        String title = "Prośba o kontakt telefoniczny";
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, message);
    }

    public void sendTaskRatingEmail(String message) {
        String title = "Nowa ocena zadania";
        asyncMailSender.sendEmail(ADMIN_EMAIL, title, message);
    }
}
