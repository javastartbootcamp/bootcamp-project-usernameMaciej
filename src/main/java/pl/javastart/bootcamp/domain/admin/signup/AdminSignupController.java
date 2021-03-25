package pl.javastart.bootcamp.domain.admin.signup;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupFacade;
import pl.javastart.bootcamp.domain.signup.SignupService;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.user.User;

import java.util.HashSet;
import java.util.Set;

@RequestMapping("/admin/zapisy")
@Controller
public class AdminSignupController {

    private static final String BUTTON_ACCEPT = "BUTTON_ACCEPT";
    private static final String BUTTON_REJECT = "BUTTON_REJECT";
    private static final String BUTTON_GENERATE_AGREEMENT = "BUTTON_GENERATE_AGREEMENT";
    private static final String BUTTON_ADVANCE_PAID = "BUTTON_ADVANCE_PAID";
    private static final String BUTTON_FULLY_PAID = "BUTTON_FULLY_PAID";
    private static final String BUTTON_SEND_MESSAGE = "BUTTON_SEND_MESSAGE";
    private static final String MESSAGE_INVALID_DATA_TITLE = "Błąd w danych";
    private static final String MESSAGE_INVALID_DATA = "Wygląda na to, że w dane podane podczas rejestracji wkradł się błąd.";
    private static final String MESSAGE_NO_SIGNED_AGREEMENT_TITLE = "Brak akceptacji umowy";
    private static final String MESSAGE_NO_SIGNED_AGREEMENT = "Nie otrzymaliśmy informacji o akceptacji umowy. "
            + "Prosimy o zapoznanie się z nią oraz akceptację. Akceptacja umowy i wpłata zadatku jest wymagana w celu rejestracji na szkolenie. "
            + "W razie pytań służymy pomocą.";
    private static final String MESSAGE_NO_ADVANCE_TITLE = "Przypomnienie o zadatku.";
    private static final String MESSAGE_NO_ADVANCE = "Na nasze konto nie wpłynął jeszcze zadatek za szkolenie. "
            + "Zadatek jest wymagany w celu potwierdzenia rejestracji.";
    private static final String MESSAGE_NO_PAYMENT_TITLE = "Przypomnienie o płatności";
    private static final String MESSAGE_NO_PAYMENT = "Na nasze konto nie wpłynęła jeszcze opłata za szkolenie. "
            + "Prosimy o dokonanie płatności w możliwie najbliższym terminie.";

    private final SignupService signupService;
    private final SignupFacade signupFacade;
    private final TrainingService trainingService;

    public AdminSignupController(SignupService signupService, SignupFacade signupFacade, TrainingService trainingService) {
        this.signupService = signupService;
        this.signupFacade = signupFacade;
        this.trainingService = trainingService;
    }

    @GetMapping("/{id}")
    public String signup(@PathVariable Long id, Model model) {
        Signup signup = signupService.findById(id).orElseThrow(ResourceNotFoundException::new);
        model.addAttribute("signup", signup);

        String title = "";
        String message = "";
        Set<String> visibleButtons = new HashSet<>();

        switch (signup.getStatus()) {
            case NEW:
                visibleButtons.add(BUTTON_ACCEPT);
                visibleButtons.add(BUTTON_REJECT);
                visibleButtons.add(BUTTON_SEND_MESSAGE);
                title = MESSAGE_INVALID_DATA_TITLE;
                message = MESSAGE_INVALID_DATA;
                break;
            case APPROVED:
                visibleButtons.add(BUTTON_REJECT);
                visibleButtons.add(BUTTON_SEND_MESSAGE);
                visibleButtons.add(BUTTON_GENERATE_AGREEMENT);
                title = MESSAGE_NO_SIGNED_AGREEMENT_TITLE;
                message = MESSAGE_NO_SIGNED_AGREEMENT;
                break;
            case RESERVE:
                visibleButtons.add(BUTTON_REJECT);
                break;
            case AGREEMENT_SIGNED:
                visibleButtons.add(BUTTON_REJECT);
                visibleButtons.add(BUTTON_ADVANCE_PAID);
                visibleButtons.add(BUTTON_FULLY_PAID);
                visibleButtons.add(BUTTON_SEND_MESSAGE);
                title = MESSAGE_NO_ADVANCE_TITLE;
                message = MESSAGE_NO_ADVANCE;
                break;
            case ADVANCE_PAID:
                visibleButtons.add(BUTTON_REJECT);
                visibleButtons.add(BUTTON_ADVANCE_PAID);
                visibleButtons.add(BUTTON_FULLY_PAID);
                visibleButtons.add(BUTTON_SEND_MESSAGE);
                title = MESSAGE_NO_PAYMENT_TITLE;
                message = MESSAGE_NO_PAYMENT;
                break;
            case PAID:
                visibleButtons.add(BUTTON_REJECT);
                visibleButtons.add(BUTTON_SEND_MESSAGE);
                break;
            case REJECTED:
                break;
            default:
                break;
        }

        model.addAttribute("visibleButtons", visibleButtons);
        model.addAttribute("title", title);
        model.addAttribute("message", message);

        return "admin/signup/signup";
    }

    @GetMapping("/{id}/akceptacja")
    public String acceptPage(@PathVariable Long id, Model model) {
        Signup signup = signupFacade.findByIdOrThrow(id);
        SignupAcceptDto dto = new SignupAcceptDto();
        dto.setSignupId(id);
        User user = signup.getUser();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress(user.getAddress());
        dto.setPostalCode(user.getPostalCode());
        dto.setCity(user.getCity());
        dto.setAdvancePaymentTo(signup.getAdvancePaymentTo());
        dto.setFullPaymentFrom(signup.getTraining().getFullPaymentFrom());
        dto.setFullPaymentTo(signup.getTraining().getFullPaymentTo());
        dto.setDeposit(signup.getDeposit());
        dto.setPrice(signup.getPrice());
        dto.setCustomPaymentInfo(signup.getCustomPaymentInfo());
        dto.setPostalCode(user.getPostalCode());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());

        model.addAttribute("dto", dto);
        return "admin/signup/signupAccept";
    }

    @PostMapping("/{id}/akceptacja")
    public String accept(SignupAcceptDto signupAcceptDto, @PathVariable Long id) {
        signupFacade.updateDataAndAccept(id, signupAcceptDto);
        return "redirect:/admin/zapisy/" + id;
    }

    @GetMapping("/{id}/odrzuc")
    public String reject(@PathVariable Long id, Model model) {
        Signup signup = signupService.findById(id).orElseThrow(ResourceNotFoundException::new);
        model.addAttribute("signup", signup);
        return "admin/signup/signupReject";
    }

    @PostMapping("/{id}/generujWzorUmowy")
    public String generateAgreement(@PathVariable Long id) {
        signupFacade.generateAgreementAsync(id);
        return "redirect:/admin/zapisy/" + id;
    }

    @PostMapping("/{id}/ustawIdKanalu")
    public String setSlackChannelId(Signup signup) {
        signupFacade.updateSignupSlackChannel(signup);
        return "redirect:/admin/zapisy/" + signup.getId();
    }

    @PostMapping("/{id}/potwierdzZaplateZadatku")
    public String confirmAdvancePaid(@PathVariable Long id) {
        signupFacade.advancePaid(id);
        return "redirect:/admin/zapisy/" + id;
    }

    @PostMapping("/{id}/potwierdzPelnaZaplate")
    public String confirmFullyPaid(@PathVariable Long id) {
        signupFacade.fullyPaid(id);
        return "redirect:/admin/zapisy/" + id;
    }

    @PostMapping("/odrzuc")
    public String rejectSignup(@RequestParam Long signupId, @RequestParam String reason) {
        signupFacade.rejectSignup(signupId, reason);
        return "redirect:/admin/zapisy/" + signupId;
    }

    @PostMapping("{id}/dodajNotatke")
    public String addNote(@PathVariable Long id, @RequestParam String text) {
        signupFacade.addNote(id, text);
        return "redirect:/admin/zapisy/" + id;
    }

    @PostMapping("{id}/wyslijWiadomosc")
    public String sendMessage(@PathVariable Long id, @RequestParam String title, @RequestParam String text) {
        signupFacade.sendMessage(id, title, text);
        return "redirect:/admin/zapisy/" + id;
    }

    @GetMapping("/dodaj")
    public String addSignup(@RequestParam Long trainingId, Model model) {

        model.addAttribute("trainings", trainingService.findAllActive());
        ManualTrainingSignupDto dto = new ManualTrainingSignupDto();
        dto.setTrainingId(trainingId);
        model.addAttribute("signupDto", dto);

        return "admin/signup/signupAddManually";
    }

    @PostMapping("/{id}/dostep")
    public String toggleAccess(@PathVariable Long id, @RequestParam Boolean access) {
        signupFacade.updateAccess(id, access);
        return "redirect:/admin/zapisy/" + id;
    }
}
