package pl.javastart.bootcamp.domain.admin.training.task;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.javastart.bootcamp.domain.admin.task.TaskService;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTask;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTaskEntry;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTaskSolutionService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AdminLessonTaskController {

    private final LessonTaskService lessonTaskService;
    private final TrainingService trainingService;
    private final UserService userService;
    private final TaskService taskService;
    private final UserTaskSolutionService userTaskSolutionService;

    public AdminLessonTaskController(LessonTaskService lessonTaskService,
                                     TrainingService trainingService,
                                     UserService userService,
                                     TaskService taskService,
                                     UserTaskSolutionService userTaskSolutionService) {
        this.lessonTaskService = lessonTaskService;
        this.trainingService = trainingService;
        this.userService = userService;
        this.taskService = taskService;
        this.userTaskSolutionService = userTaskSolutionService;
    }

    @GetMapping("/admin/lekcje/zadania/{id}")
    public String displayTask(@PathVariable Long id, Model model) {
        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(id);
        model.addAttribute("lessonTask", lessonTask);

        List<User> participants = trainingService.findAllParticipants(lessonTask.getLesson().getTraining());

        List<UserTask> userTasks = lessonTaskService.findWithResultsForTask(lessonTask);
        Map<User, List<UserTask>> userToUserTaskMap = userTasks.stream().collect(Collectors.groupingBy(UserTask::getUser));
        for (User participant : participants) {
            if (!userToUserTaskMap.containsKey(participant)) {
                UserTask userTask = new UserTask();
                userTask.setUser(participant);
                userTasks.add(userTask);
            }
        }

        List<UserTask> sortedUserTasks = userTasks.stream().sorted(Comparator.comparing(ut -> ut.getUser().getLastName())).collect(Collectors.toList());
        model.addAttribute("userTasks", sortedUserTasks);

        return "admin/lesson/task/lessonTaskDisplay";
    }

    @GetMapping("/admin/lekcje/zadania/dodaj")
    public String addTaskForm(@RequestParam Long lessonId, Model model) {
        AddLessonTaskDto task = new AddLessonTaskDto();
        task.setLessonId(lessonId);
        task.setDeadline(LocalDateTime.now().plusDays(5).withHour(23).withMinute(59));
        model.addAttribute("task", task);
        model.addAttribute("mode", "add");
        model.addAttribute("tasks", taskService.findAllSortedAndNotArchived());

        return "admin/lesson/task/lessonTaskAdd";
    }

    @PostMapping("/admin/lekcje/zadania/dodaj")
    public String addTask(AddLessonTaskDto addLessonTaskDto) {
        lessonTaskService.save(addLessonTaskDto);
        return "redirect:/admin/lekcje/" + addLessonTaskDto.getLessonId();
    }

    @GetMapping("/admin/lekcje/zadania/{id}/edytuj")
    public String editTaskForm(@PathVariable Long id, Model model) {
        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(id);
        model.addAttribute("lessonTask", lessonTask);
        model.addAttribute("mode", "edit");
        return "admin/lesson/task/lessonTaskEdit";
    }

    @PostMapping("/admin/lekcje/zadania/edytuj")
    public String editTask(LessonTask task) {
        lessonTaskService.save(task);
        return "redirect:/admin/lekcje/" + task.getLesson().getId();
    }

    @GetMapping("/admin/lekcje/zadania/{id}/usun")
    public String deleteTask(@PathVariable Long id) {
        LessonTask task = lessonTaskService.findByIdOrThrow(id);
        lessonTaskService.delete(task);
        return "redirect:/admin/lekcje/" + task.getLesson().getId();
    }

    @GetMapping("/admin/lekcje/zadania/{id}/ocena")
    public String displayPointsForm(@PathVariable Long id, @RequestParam Long userId, Model model) {

        Optional<UserTask> userTaskOptional = lessonTaskService.findUserTaskByLessonTaskIdAndUserId(id, userId);
        UserTask userTask;
        if (userTaskOptional.isPresent()) {
            userTask = userTaskOptional.get();
        } else {
            userTask = new UserTask();
            LessonTask lessonTask = lessonTaskService.findByIdOrThrow(id);
            userTask.setLessonTask(lessonTask);
            userTask.setUser(userService.findByIdOrThrow(userId));
            userTask.setDeadline(lessonTask.getDeadline());
            lessonTaskService.saveUserTask(userTask);
        }

        model.addAttribute("userTask", userTask);

        if (userTask.getId() != null) {
            List<UserTaskEntry> userTaskSolutionEntries = userTaskSolutionService.findUserTaskEntriesSortedByDate(userTask);
            model.addAttribute("userTaskSolutionEntries", userTaskSolutionEntries);
        }

        return "admin/lesson/task/taskResult";
    }

    @PostMapping("/admin/lekcje/zadania/ocena")
    public String savePoints(UserTask userTask) {
        userTaskSolutionService.handleRatingSendByTrainer(userTask);
        return "redirect:/admin/lekcje/zadania/" + userTask.getLessonTask().getId() + "/ocena?userId=" + userTask.getUser().getId();
    }

    @GetMapping("/admin/lekcje/zadania/{id}/obowiazkowe")
    public String toggleMandatory(@PathVariable Long id, @RequestParam(name = "zmien") Boolean mandatory) {
        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(id);
        lessonTask.setMandatory(mandatory);
        lessonTaskService.save(lessonTask);
        return "redirect:/admin/lekcje/" + lessonTask.getLesson().getId();
    }
}
