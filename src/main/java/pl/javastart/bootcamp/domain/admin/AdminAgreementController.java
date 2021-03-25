package pl.javastart.bootcamp.domain.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.javastart.bootcamp.domain.agreement.AgreementService;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupFacade;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.utils.IpReceiver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class AdminAgreementController {

    private AgreementService agreementService;
    private SignupFacade signupFacade;
    private TrainingService trainingService;
    private IpReceiver ipReceiver;

    public AdminAgreementController(AgreementService agreementService, SignupFacade signupFacade, TrainingService trainingService, IpReceiver ipReceiver) {
        this.agreementService = agreementService;
        this.signupFacade = signupFacade;
        this.trainingService = trainingService;
        this.ipReceiver = ipReceiver;
    }

    @GetMapping("/admin/umowa")
    public String adminAgreement(Model model) {
        model.addAttribute("content", agreementService.getAgreementContent());
        return "admin/agreement";
    }

    @PostMapping("/admin/umowa")
    public String updateAgreement(@RequestParam String content) {
        agreementService.updateAgreement(content);
        return "redirect:/admin/umowa";
    }

    @GetMapping("/admin/umowa/test")
    public String agreementTest(Model model) {
        AgreementTestData agreementTestData = new AgreementTestData();
        agreementTestData.setFirstName("Jan");
        agreementTestData.setLastName("Kowalski");
        agreementTestData.setAddress("Kościelna 12b/4");
        agreementTestData.setCity("Wrocław");
        agreementTestData.setPostalCode("50-010");
        agreementTestData.setEmail("jan.kowalski@example.com");
        agreementTestData.setPhoneNumber("699499402");
        agreementTestData.setDeposit(BigDecimal.valueOf(1000));
        agreementTestData.setPrice(BigDecimal.valueOf(5000));
        model.addAttribute("testData", agreementTestData);
        model.addAttribute("trainings", trainingService.findAllActive());
        return "admin/agreementTest";
    }

    @PostMapping("/admin/umowa/test")
    public String agreementTest(AgreementTestData testData, Model model, HttpServletRequest request) {
        User user = new User();
        user.setFirstName(testData.getFirstName());
        user.setLastName(testData.getLastName());
        user.setEmail(testData.getEmail());
        user.setPhoneNumber(testData.getPhoneNumber());
        user.setAddress(testData.getAddress());
        user.setCity(testData.getCity());
        user.setPostalCode(testData.getPostalCode());

        Signup signup = new Signup();
        signup.setDeposit(testData.getDeposit());
        signup.setPrice(testData.getPrice());
        signup.setUser(user);
        signup.setTraining(testData.getTraining());
        signup.setAgreementSigningIp(ipReceiver.getClientIpAddr(request));

        if (testData.getAdvancePaymentTo() != null) {
            signup.setAdvancePaymentToOverride(testData.getAdvancePaymentTo());
        }

        if (testData.getFullPaymentFrom() != null) {
            signup.setFullPaymentFromOverride(testData.getFullPaymentFrom());
        }

        if (testData.getFullPaymentTo() != null) {
            signup.setFullPaymentToOverride(testData.getFullPaymentTo());
        }

        signupFacade.calculateAdvancePaymentTo(signup, testData.getToday());

        String filename = agreementService.prepareSignedAgreementForSignup(signup, LocalDate.now());
        model.addAttribute("fileName", filename);
        return "admin/agreementTestResult";
    }
}
