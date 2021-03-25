package pl.javastart.bootcamp.domain.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/reset-hasla")
    public String passwordReset() {
        return "passwordReset/passwordReset";
    }

    @PostMapping("/reset-hasla")
    public String passwordReset(String email) {
        boolean success = userService.sendPasswordResetLink(email);
        if (success) {
            return "passwordReset/passwordResetSuccess";
        } else {
            return "redirect:/reset-hasla?error=true";
        }
    }

    @GetMapping("/reset-hasla-koniec")
    public String passwordResetFinal() {
        return "passwordReset/passwordResetFinal";
    }

    @PostMapping("/reset-hasla-koniec")
    public String passwordResetFinal(String key, String password, String passwordRepeat) {
        String errorUrl = "redirect:/reset-hasla-koniec?key=" + key + "&error=true";

        if (!password.equals(passwordRepeat)) {
            return errorUrl;
        }

        boolean success = userService.changeUserPassword(key, password);

        if (success) {
            return "passwordReset/passwordResetFinalSuccess";
        }

        return errorUrl;
    }

    @GetMapping("/potwierdzKonto")
    public String confirmAccount(@RequestParam String code, Model model) {
        ActivationResult activationResult = userService.activateAccount(code);
        model.addAttribute("result", activationResult.name());
        return "emailConfirmed";
    }

    @GetMapping("/konto/ustawienia")
    public String accountSettings(Model model, Principal principal) {
        User user = userService.findByEmailOrThrow(principal.getName());
        model.addAttribute("user", user);
        return "account/settings";
    }

    @PostMapping("/konto/ustawienia/github")
    public String updateGithubUsername(@RequestParam String githubUsername, Principal principal) {
        userService.updateGithubUsername(principal.getName(), githubUsername);
        return "redirect:/konto/ustawienia";
    }

    @GetMapping("/konto/zmiana-hasla")
    public String changePassword() {
        return "account/changePassword";
    }

    @PostMapping("/konto/zmiana-hasla")
    public String changePassword(@RequestParam String newPassword,
                                 @RequestParam String newPassword2,
                                 Principal principal, Model model) {

        if (newPassword.length() < 8) {
            model.addAttribute("message", "Hasło powinno mieć co najmniej 8 znaków.");
            return "account/changePassword";
        }

        if (newPassword.equals(newPassword2)) {
            try {
                userService.changePassword(principal.getName(), newPassword);
            } catch (InvalidPasswordException e) {
                model.addAttribute("message", "Podałeś niepoprawne aktualne hasło");
                return "account/changePassword";
            }
        } else {
            model.addAttribute("message", "Wpisane różnią się od siebie");
            return "account/changePassword";
        }

        return "account/changePasswordSuccess";
    }

    @GetMapping("/konto/usun")
    public String deleteAccount(Model model) {
        String code = UUID.randomUUID().toString().substring(0, 8);
        model.addAttribute("code", code);
        return "account/deleteAccount";
    }

    @PostMapping("/konto/usun")
    public String deleteAccount(@RequestParam String code, @RequestParam String inputCode, Principal principal) {
        if (code.equals(inputCode)) {
            userService.deleteAccountByHimself(principal.getName());
            return "redirect:/logout";
        } else {
            return "redirect:/konto/usun";
        }
    }

}
