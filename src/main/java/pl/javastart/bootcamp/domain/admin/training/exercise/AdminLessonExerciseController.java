package pl.javastart.bootcamp.domain.admin.training.exercise;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.javastart.bootcamp.domain.admin.task.TaskService;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExercise;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExerciseService;

@Controller
public class AdminLessonExerciseController {

    private final LessonExerciseService lessonExerciseService;
    private final TaskService taskService;

    public AdminLessonExerciseController(LessonExerciseService lessonExerciseService,
                                         TaskService taskService) {
        this.lessonExerciseService = lessonExerciseService;
        this.taskService = taskService;
    }

    @GetMapping("/admin/lekcje/cwiczenia/{id}")
    public String displayTask(@PathVariable Long id, Model model) {
        LessonExercise lessonExercise = lessonExerciseService.findByIdOrThrow(id);
        model.addAttribute("lessonExercise", lessonExercise);
        return "admin/lesson/exercise/lessonExerciseDisplay";
    }

    @GetMapping("/admin/lekcje/cwiczenia/dodaj")
    public String addTaskForm(@RequestParam Long lessonId, Model model) {
        AddLessonExerciseDto task = new AddLessonExerciseDto();
        task.setLessonId(lessonId);
        model.addAttribute("task", task);
        model.addAttribute("mode", "add");
        model.addAttribute("tasks", taskService.findAllSortedAndNotArchived());

        return "admin/lesson/exercise/lessonExerciseAdd";
    }

    @PostMapping("/admin/lekcje/cwiczenia/dodaj")
    public String addTask(AddLessonExerciseDto addLessonExerciseDto) {
        lessonExerciseService.save(addLessonExerciseDto);
        return "redirect:/admin/lekcje/" + addLessonExerciseDto.getLessonId();
    }

    @GetMapping("/admin/lekcje/cwiczenia/{id}/edytuj")
    public String editTaskForm(@PathVariable Long id, Model model) {
        LessonExercise lessonExercise = lessonExerciseService.findByIdOrThrow(id);
        model.addAttribute("lessonExercise", lessonExercise);
        model.addAttribute("mode", "edit");
        return "admin/lesson/exercise/lessonExerciseEdit";
    }

    @PostMapping("/admin/lekcje/cwiczenia/edytuj")
    public String editTask(LessonExercise lessonExercise) {
        lessonExerciseService.save(lessonExercise);
        return "redirect:/admin/lekcje/" + lessonExercise.getLesson().getId();
    }

    @GetMapping("/admin/lekcje/cwiczenia/{id}/usun")
    public String deleteExercise(@PathVariable Long id) {
        LessonExercise task = lessonExerciseService.findByIdOrThrow(id);
        lessonExerciseService.delete(task);
        return "redirect:/admin/lekcje/" + task.getLesson().getId();
    }

}
