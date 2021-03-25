package pl.javastart.bootcamp.domain.user.training.lesson;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;
import pl.javastart.bootcamp.domain.training.lesson.LessonService;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;
import pl.javastart.bootcamp.domain.user.training.lesson.task.TaskWithResultDto;

import java.security.Principal;
import java.util.List;

@Controller
public class UserLessonController {

    private LessonService lessonService;
    private LessonTaskService lessonTaskService;
    private UserService userService;

    public UserLessonController(LessonService lessonService, LessonTaskService lessonTaskService, UserService userService) {
        this.lessonService = lessonService;
        this.lessonTaskService = lessonTaskService;
        this.userService = userService;
    }

    @GetMapping("/konto/zajecia/{id}")
    public String lesson(@PathVariable Long id, Model model, Principal principal) {
        Lesson lesson = lessonService.findByIdOrThrow(id);
        model.addAttribute("lesson", lesson);

        User user = userService.findByEmailOrThrow(principal.getName());
        List<TaskWithResultDto> tasks = lessonTaskService.findWithResultForLessonForUser(lesson, user);
        model.addAttribute("tasks", tasks);

        model.addAttribute("exercises", lesson.getLessonExercises());

        return "account/training/lesson";
    }
}
