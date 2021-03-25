package pl.javastart.bootcamp.domain.agreement;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.javastart.bootcamp.config.JavaStartProperties;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupFacade;
import pl.javastart.bootcamp.utils.IpReceiver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.List;

@Controller
public class AgreementController {

    private JavaStartProperties javaStartProperties;
    private SignupFacade signupFacade;
    private IpReceiver ipReceiver;

    public AgreementController(JavaStartProperties javaStartProperties, SignupFacade signupFacade, IpReceiver ipReceiver) {
        this.javaStartProperties = javaStartProperties;
        this.signupFacade = signupFacade;
        this.ipReceiver = ipReceiver;
    }

    @GetMapping(value = "/umowa/{filename}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {

        File file = new File(javaStartProperties.getAgreementsDir(), filename);
        if (file.exists()) {
            Path path = file.toPath();
            return ResponseEntity.ok(Files.readAllBytes(path));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/konto/zapisy/{id}/umowa")
    public String agreement(@PathVariable Long id, Model model, Principal principal) {
        List<Signup> signups = signupFacade.findByUserEmail(principal.getName());
        Signup signup = signupFacade.findByIdOrThrow(id);
        if (!signups.contains(signup)) {
            return "redirect:/konto/zapisy";
        }

        signupFacade.registerAgreementSiteVisit(id);
        model.addAttribute("signupId", id);
        model.addAttribute("agreementAcceptation", new AgreementAcceptationDto());
        String agreementFileName = signupFacade.getAgreementTemplateFileNameForSignupId(id);
        model.addAttribute("agreementFileName", agreementFileName);
        model.addAttribute("signup", signupFacade.findByIdOrThrow(id));
        return "account/agreement";
    }

    @PostMapping("/konto/zapisy/{id}/umowa/akceptuje")
    public String acceptAgreement(@Valid @ModelAttribute("agreementAcceptation") AgreementAcceptationDto acceptationDto,
                                  BindingResult bindingResult,
                                  Model model, HttpServletRequest request,
                                  @PathVariable Long id,
                                  Principal principal) {
        List<Signup> signups = signupFacade.findByUserEmail(principal.getName());
        Signup signup = signupFacade.findByIdOrThrow(id);
        if (!signups.contains(signup)) {
            return "redirect:/konto/zapisy";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("agreementAcceptation", acceptationDto);
            return "account/agreement";
        }

        String ip = ipReceiver.getClientIpAddr(request);

        signupFacade.processAgreementSignedAsync(id, ip);
        return "account/agreementSigned";
    }


}
