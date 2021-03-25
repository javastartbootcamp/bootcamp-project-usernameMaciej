package pl.javastart.bootcamp.domain.admin.training.rating;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;
import pl.javastart.bootcamp.domain.user.training.UserTrainingResultDto;
import pl.javastart.bootcamp.domain.user.training.UserTrainingService;

@Controller
public class AdminRatingController {

    private final UserService userService;
    private final TrainingService trainingService;
    private final RatingService ratingService;
    private final UserTrainingService userTrainingService;

    public AdminRatingController(UserService userService,
                                 TrainingService trainingService,
                                 RatingService ratingService,
                                 UserTrainingService userTrainingService) {
        this.userService = userService;
        this.trainingService = trainingService;
        this.ratingService = ratingService;
        this.userTrainingService = userTrainingService;
    }

    @GetMapping("/admin/oceny")
    public String userTasks(@RequestParam Long userId, @RequestParam Long trainingId, Model model) {

        User user = userService.findByIdOrThrow(userId);
        Training training = trainingService.findByIdOrThrow(trainingId);

        TaskWithRatingWrapperDto dto = ratingService.findTasksWithRatings(user, training);

        model.addAttribute("user", user);
        model.addAttribute("training", training);
        model.addAttribute("tasksWithRatings", dto);

        UserTrainingResultDto trainingResultDto = userTrainingService.calculateResult(trainingId, user);
        model.addAttribute("trainingResult", trainingResultDto);

        return "admin/rating/rating";
    }

    @PostMapping("/admin/oceny")
    public String userTasksSave(TaskWithRatingWrapperDto dto) {
        ratingService.save(dto);
        return "redirect:/admin/oceny?userId=" + dto.getUserId() + "&trainingId=" + dto.getTrainingId();
    }

}
