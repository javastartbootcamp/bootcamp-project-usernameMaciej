package pl.javastart.bootcamp.domain.admin.task;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;

@Controller
@RequestMapping("/admin/zadania")
public class AdminTaskController {

    private final TaskService taskService;

    public AdminTaskController(TaskService taskService, LessonTaskService lessonTaskService) {
        this.taskService = taskService;
    }

    @GetMapping("")
    public String taskList(Model model) {
        model.addAttribute("tasks", taskService.findAllSortedAndNotArchived());
        return "admin/task/taskList";
    }

    @GetMapping("/{id}")
    public String previewTask(@PathVariable Long id, Model model) {
        Task task = taskService.findByIdOrThrow(id);
        model.addAttribute("task", task);
        return "admin/task/task";
    }

    @GetMapping("/dodaj")
    public String addTaskForm(Model model) {
        Task task =  taskService.prepareTaskWithSortOrder();
        model.addAttribute("task", task);
        model.addAttribute("mode", "add");
        return "admin/task/taskAddOrEdit";
    }

    @PostMapping("/dodaj")
    public String addTask(Task task) {
        taskService.save(task);
        return "redirect:/admin/zadania/" + task.getId();
    }

    @GetMapping("/{id}/edytuj")
    public String editTaskForm(@PathVariable Long id, Model model) {
        Task task = taskService.findByIdOrThrow(id);
        model.addAttribute("task", task);
        model.addAttribute("mode", "edit");
        return "admin/task/taskAddOrEdit";
    }

    @PostMapping("/edytuj")
    public String editTask(Task task) {
        taskService.save(task);
        return "redirect:/admin/zadania/" + task.getId();
    }

    @GetMapping("/{id}/archiwizuj")
    public String archiveTask(@PathVariable Long id) {
        taskService.archiveTaskById(id);
        return "redirect:/admin/zadania";
    }

    @GetMapping("/{id}/usun")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return "redirect:/admin/zadania";
    }
}
