package pl.javastart.bootcamp.domain.agreement;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupFacade;

import java.util.List;

@Service
public class AgreementGenerationScheduler {

    private SignupFacade signupFacade;

    public AgreementGenerationScheduler(SignupFacade signupFacade) {
        this.signupFacade = signupFacade;
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void generateAgreementTemplates() {
        List<Signup> allWithAgreementSigned = signupFacade.findAllApproved();
        for (Signup signup : allWithAgreementSigned) {
            signupFacade.generateAgreementSync(signup.getId());
        }
    }
}
