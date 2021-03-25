package pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.javastart.bootcamp.domain.admin.task.rating.TaskRatingDto;
import pl.javastart.bootcamp.domain.admin.task.rating.TaskRatingService;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupService;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExercise;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExerciseService;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserTaskController {

    private final LessonTaskService lessonTaskService;
    private final LessonExerciseService lessonExerciseService;
    private final UserService userService;
    private final UserTaskSolutionService userTaskSolutionService;
    private final SignupService signupService;
    private final TaskRatingService taskRatingService;

    public UserTaskController(LessonTaskService lessonTaskService,
                              LessonExerciseService lessonExerciseService,
                              UserService userService,
                              UserTaskSolutionService userTaskSolutionService,
                              SignupService signupService,
                              TaskRatingService taskRatingService) {
        this.lessonTaskService = lessonTaskService;
        this.lessonExerciseService = lessonExerciseService;
        this.userService = userService;
        this.userTaskSolutionService = userTaskSolutionService;
        this.signupService = signupService;
        this.taskRatingService = taskRatingService;
    }

    @GetMapping("/konto/zadanie/{id}")
    public String task(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByEmailOrThrow(principal.getName());
        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(id);

        model.addAttribute("lessonTask", lessonTask);
        UserTask userTask = lessonTaskService.findUserTaskByLessonTaskIdAndUserId(id, user.getId()).orElse(null);
        model.addAttribute("userTask", userTask);
        model.addAttribute("type", "task");
        model.addAttribute("githubUsername", user.getGithubUsername());
        model.addAttribute("taskRating", taskRatingService.findByUserAndTaskIdOrElseNew(user, lessonTask.getTask()));

        Signup signup = signupService.findSignupForUserAndLessonTask(user, lessonTask);
        model.addAttribute("signup", signup);

        boolean isAfterDeadline;
        if (userTask != null) {
            isAfterDeadline = LocalDateTime.now().isAfter(userTask.getDeadline());
        } else {
            isAfterDeadline = LocalDateTime.now().isAfter(lessonTask.getDeadline());
        }

        model.addAttribute("isAfterDeadline", isAfterDeadline);

        UserTaskSolutionDto userTaskSolution = new UserTaskSolutionDto();
        userTaskSolution.setLessonTaskId(id);
        if (userTask != null) {
            userTaskSolution.setUrl(userTask.getSolutionUrl());
        }

        model.addAttribute("userTaskSolution", userTaskSolution);

        if (userTask != null) {
            List<UserTaskEntry> userTaskSolutionEntries = userTaskSolutionService.findUserTaskEntriesSortedByDate(userTask);
            model.addAttribute("userTaskSolutionEntries", userTaskSolutionEntries);
        }

        boolean solutionVisible = isAfterDeadline;
        if (userTask != null && userTask.getPoints() != null) {
            if (BigDecimal.valueOf(lessonTask.getTask().getPoints()).compareTo(userTask.getPoints()) == 0) {
                solutionVisible = true;
            }
        }

        model.addAttribute("solutionVisible", solutionVisible);

        return "account/training/task";
    }

    @GetMapping("/konto/cwiczenie/{id}")
    public String excercise(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByEmailOrThrow(principal.getName());
        LessonExercise lessonTask = lessonExerciseService.findByIdOrThrow(id);
        model.addAttribute("lessonTask", lessonTask);
        model.addAttribute("type", "exercise");
        model.addAttribute("solutionVisible", true);
        model.addAttribute("taskRating", taskRatingService.findByUserAndTaskIdOrElseNew(user, lessonTask.getTask()));
        return "account/training/task";
    }

    @PostMapping("/konto/zadanie/rozwiazanie")
    public String userSolution(UserTaskSolutionDto userTaskSolutionDto) {

        userTaskSolutionService.handleSolutionSentByUser(userTaskSolutionDto);

        return "redirect:/konto/zadanie/" + userTaskSolutionDto.getLessonTaskId();
    }

    @PostMapping("/konto/zadanie/przedluz")
    public String extendDeadline(@RequestParam Long lessonTaskId, Principal principal) {
        User user = userService.findByEmailOrThrow(principal.getName());
        userTaskSolutionService.extendDeadlineForLessonTaskAndUser(lessonTaskId, user);
        return "redirect:/konto/zadanie/" + lessonTaskId;
    }

    @PostMapping("/konto/cwiczenie/ocen")
    public String extendDeadline(TaskRatingDto taskRating,
                                 @RequestParam String redirectUrl,
                                 Principal principal) {
        User user = userService.findByEmailOrThrow(principal.getName());
        taskRatingService.createOrUpdateRating(taskRating, user);
        return "redirect:" + redirectUrl;
    }
}
