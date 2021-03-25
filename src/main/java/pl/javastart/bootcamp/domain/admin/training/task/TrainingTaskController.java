package pl.javastart.bootcamp.domain.admin.training.task;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTaskSolutionService;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTaskWithLastEntryDto;

import java.util.List;

@Controller
public class TrainingTaskController {

    private final UserTaskSolutionService userTaskSolutionService;

    public TrainingTaskController(UserTaskSolutionService userTaskSolutionService) {
        this.userTaskSolutionService = userTaskSolutionService;
    }

    @GetMapping("/admin/szkolenia/{trainingId}/zadania")
    public String trainingTaskToCheck(@PathVariable Long trainingId, Model model) {

        List<UserTaskWithLastEntryDto> notChecked = userTaskSolutionService.findNotCheckedForTrainingId(trainingId);

        model.addAttribute("notChecked", notChecked);

        return "admin/trainingTasks";
    }
}
