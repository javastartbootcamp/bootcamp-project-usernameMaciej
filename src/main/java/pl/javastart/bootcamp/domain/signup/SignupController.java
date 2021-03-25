package pl.javastart.bootcamp.domain.signup;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingService;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class SignupController {

    private TrainingService trainingService;
    private SignupFacade signupFacade;

    public SignupController(TrainingService trainingService, SignupFacade signupFacade) {
        this.trainingService = trainingService;
        this.signupFacade = signupFacade;
    }

    @GetMapping("/szkolenie/{url}/{id}")
    public String training(@PathVariable Long id, Model model) {

        Optional<Training> trainingOptional = trainingService.findById(id);

        if (trainingOptional.isPresent()) {
            Training training = trainingOptional.get();
            model.addAttribute("training", training);
            long activeSignupsCount = training.getSignups().stream().filter(s -> s.getStatus() != SignupStatus.REJECTED).count();
            model.addAttribute("activeSignupsCount", activeSignupsCount);
            SignupDto signupDto = new SignupDto();
            signupDto.setTrainingId(training.getId());
            model.addAttribute("signup", signupDto);
        } else {
            throw new ResourceNotFoundException();
        }

        return "training";
    }

    @PostMapping("/zgloszenie")
    public String signup(@Valid @ModelAttribute("signup") SignupDto signup, BindingResult bindingResult, Model model) {
        Long trainingId = signup.getTrainingId();
        Optional<Training> trainingOptional = trainingService.findById(trainingId);
        if (!trainingOptional.isPresent()) {
            throw new ResourceNotFoundException();
        }

        Training training = trainingOptional.get();
        if (bindingResult.hasErrors()) {
            model.addAttribute("training", training);
            signup.setTrainingId(training.getId());
            model.addAttribute("signup", signup);
            return "training";
        }

        signupFacade.processSignup(signup);

        return "redirect:/zgloszenie-udane";
    }

    @GetMapping("/zgloszenie-udane")
    public String signupSuccess() {
        return "signupSuccess";
    }
}
