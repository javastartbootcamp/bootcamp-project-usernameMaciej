package pl.javastart.bootcamp.domain.home;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.javastart.bootcamp.mail.MailService;

@RestController
public class PhoneCallController {

    private MailService mailService;

    public PhoneCallController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/api/phone-call")
    public void requestPhoneCall(@RequestBody PhoneCallRequestDto phoneCallRequest) {
        mailService.sendPhoneRequestMail(phoneCallRequest.getFirstName(), phoneCallRequest.getPhoneNumber(), phoneCallRequest.getContactDate());
    }

}
