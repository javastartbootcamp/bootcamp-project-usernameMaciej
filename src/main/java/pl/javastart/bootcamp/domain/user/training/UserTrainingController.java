package pl.javastart.bootcamp.domain.user.training;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupFacade;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.training.lesson.LessonService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;
import pl.javastart.bootcamp.domain.user.training.lesson.LessonWithPointsDto;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserTrainingController {

    private final SignupFacade signupFacade;
    private final LessonService lessonService;
    private final UserService userService;
    private final UserTrainingService userTrainingService;
    private final TrainingService trainingService;

    public UserTrainingController(SignupFacade signupFacade,
                                  LessonService lessonService,
                                  UserService userService,
                                  UserTrainingService userTrainingService,
                                  TrainingService trainingService) {
        this.signupFacade = signupFacade;
        this.lessonService = lessonService;
        this.userService = userService;
        this.userTrainingService = userTrainingService;
        this.trainingService = trainingService;
    }

    @GetMapping("/konto/szkolenia")
    public String userTrainings(Principal principal, Model model) {
        List<Signup> signups;
        if (userService.isCurrentUserAdmin()) {
            List<Training> allActive = trainingService.findAllInProgress();
            signups = allActive.stream().map(training -> {
                Signup signup = new Signup();
                signup.setTraining(training);
                return signup;
            }).collect(Collectors.toList());
        } else {
            signups = signupFacade.findWithAccessByUserEmail(principal.getName());
        }
        if (signups.size() == 1) {
            Signup signup = signups.get(0);
            return "redirect:/konto/szkolenia/" + signup.getTraining().getId();
        } else {
            model.addAttribute("signups", signups);
            return "account/training/trainings";
        }
    }

    @GetMapping("/konto/szkolenia/{trainingId}")
    public String userTrainings(@PathVariable Long trainingId, Principal principal, Model model) {
        User user = userService.findByEmailOrThrow(principal.getName());
        Training training = trainingService.findByIdOrThrow(trainingId);
        List<LessonWithPointsDto> lessons = lessonService.findLessonsForTrainingWithUser(training.getId(), user);
        model.addAttribute("training", training);
        model.addAttribute("lessons", lessons);

        UserTrainingResultDto trainingResultDto = userTrainingService.calculateResult(training.getId(), user);
        model.addAttribute("trainingResult", trainingResultDto);

        return "account/training/training";
    }


}
