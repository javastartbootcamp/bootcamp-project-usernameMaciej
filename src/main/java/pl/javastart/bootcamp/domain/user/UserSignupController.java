package pl.javastart.bootcamp.domain.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupFacade;
import pl.javastart.bootcamp.domain.signup.SignupStatus;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UserSignupController {

    private SignupFacade signupFacade;

    public UserSignupController(SignupFacade signupFacade) {
        this.signupFacade = signupFacade;
    }

    @GetMapping("/konto/zapisy")
    public String signups(Model model, Principal principal) {
        List<Signup> signups = signupFacade.findByUserEmail(principal.getName());
        model.addAttribute("signups", signups);
        return "account/signups";
    }

    @GetMapping("/konto/zapisy/{id}")
    public String signup(@PathVariable Long id, Model model, Principal principal) {
        List<Signup> signups = signupFacade.findByUserEmail(principal.getName());
        Signup signup = signupFacade.findByIdOrThrow(id);
        if (!signups.contains(signup)) {
            return "redirect:/konto/zapisy";
        }

        boolean canReject = signup.getStatus() != SignupStatus.REJECTED;
        LocalDate firstDate = LocalDate.parse(signup.getTraining().getDates().split(", ")[0], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        canReject &= LocalDate.now().isBefore(firstDate.minusDays(10));
        model.addAttribute("canReject", canReject);
        model.addAttribute("signup", signup);
        return "account/signup";
    }

    @GetMapping("/konto/zapisy/{id}/rezygnacja")
    public String signupResignation(@PathVariable Long id, Model model, Principal principal) {
        List<Signup> signups = signupFacade.findByUserEmail(principal.getName());
        Signup signup = signupFacade.findByIdOrThrow(id);
        if (!signups.contains(signup)) {
            return "redirect:/konto/zapisy";
        }

        model.addAttribute("signup", signup);
        return "account/signupResignation";
    }

    @PostMapping("/konto/zapisy/{id}/rezygnacja")
    public String signupResignationConfirm(@PathVariable Long id, @RequestParam Long signupId, Principal principal) {
        List<Signup> signups = signupFacade.findByUserEmail(principal.getName());
        Signup signup = signupFacade.findByIdOrThrow(id);
        if (!signups.contains(signup)) {
            return "redirect:/konto/zapisy";
        }
        signupFacade.rejectSignupByUser(signupId);
        return "redirect:/konto/zapisy";
    }

}
