package pl.javastart.bootcamp.domain.training.lesson.lessontask;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.admin.task.Task;
import pl.javastart.bootcamp.domain.admin.task.TaskService;
import pl.javastart.bootcamp.domain.admin.template.AddTaskToTemplateLessonDto;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplateLesson;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplateLessonRepository;
import pl.javastart.bootcamp.domain.admin.training.task.AddLessonTaskDto;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;
import pl.javastart.bootcamp.domain.training.lesson.LessonRepository;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.training.lesson.task.TaskWithResultDto;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTask;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTaskRepository;
import pl.javastart.bootcamp.utils.BigDecimalFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LessonTaskService {

    private final UserTaskRepository userTaskRepository;
    private final LessonTaskRepository lessonTaskRepository;
    private final BigDecimalFormatter bigDecimalFormatter;
    private final TaskService taskService;
    private final TrainingTemplateLessonRepository trainingTemplateLessonRepository;
    private final LessonRepository lessonRepository;

    public LessonTaskService(UserTaskRepository userTaskRepository,
                             LessonTaskRepository lessonTaskRepository,
                             BigDecimalFormatter bigDecimalFormatter,
                             TaskService taskService,
                             TrainingTemplateLessonRepository trainingTemplateLessonRepository,
                             LessonRepository lessonRepository) {
        this.userTaskRepository = userTaskRepository;
        this.lessonTaskRepository = lessonTaskRepository;
        this.bigDecimalFormatter = bigDecimalFormatter;
        this.taskService = taskService;
        this.trainingTemplateLessonRepository = trainingTemplateLessonRepository;
        this.lessonRepository = lessonRepository;
    }

    public Map<LessonTask, List<UserTask>> getTaskListMap(User user) {
        List<UserTask> userTasks = findUserTasksByUser(user);
        return userTasks.stream().collect(Collectors.groupingBy(UserTask::getLessonTask));
    }

    public List<UserTask> findUserTasksByUser(User user) {
        return userTaskRepository.findByUser(user);
    }

    public List<TaskWithResultDto> findWithResultForLessonForUser(Lesson lesson, User user) {
        Map<LessonTask, List<UserTask>> taskListMap = getTaskListMap(user);
        List<LessonTask> tasks = lesson.getLessonTasks();
        return tasks.stream().map(task -> toDto(task, taskListMap)).collect(Collectors.toList());
    }

    private TaskWithResultDto toDto(LessonTask task, Map<LessonTask, List<UserTask>> taskListMap) {
        TaskWithResultDto dto = new TaskWithResultDto();
        dto.setLessonTask(task);
        List<UserTask> userTasks = taskListMap.get(task);
        if (userTasks != null && !userTasks.isEmpty()) {
            dto.setPoints(bigDecimalFormatter.convertDecimalToString(userTasks.get(0).getPoints()));
            dto.setDeadline(userTasks.get(0).getDeadline());
        } else {
            dto.setDeadline(task.getDeadline());
        }

        return dto;
    }

    public void insert(LessonTask task) {
        lessonTaskRepository.save(task);
    }

    public LessonTask findByIdOrThrow(Long id) {
        return lessonTaskRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public List<UserTask> findWithResultsForTask(LessonTask task) {
        return userTaskRepository.findByLessonTask(task);
    }

    public Optional<UserTask> findUserTaskByLessonTaskIdAndUserId(Long taskId, Long userId) {
        return userTaskRepository.findByLessonTaskIdAndUserId(taskId, userId);
    }

    public void save(LessonTask task) {
        lessonTaskRepository.save(task);
    }

    public void save(AddLessonTaskDto addLessonTaskDto) {

        long counter = 1;

        Lesson lesson = lessonRepository.findById(addLessonTaskDto.getLessonId()).orElseThrow();

        List<LessonTask> lessonTasks = lesson.getLessonTasks();

        if (lessonTasks.size() > 0) {
            counter = lessonTasks.get(lessonTasks.size() - 1).getNumber() + 1;
        }

        for (long tasksId : addLessonTaskDto.getTaskIds()) {
            LessonTask lessonTask = new LessonTask();
            lessonTask.setLesson(lesson);
            Task task = taskService.findByIdOrThrow(tasksId);
            lessonTask.setTask(task);
            lessonTask.setNumber(counter++);
            lessonTask.setMandatory(addLessonTaskDto.getMandatoryTopicIds().contains(tasksId));
            lessonTask.setDeadline(addLessonTaskDto.getDeadline());
            lessonTaskRepository.save(lessonTask);

            task.setLastUsed(LocalDate.now());
            taskService.save(task);
        }
    }



    public void save(AddTaskToTemplateLessonDto dto) {
        long counter = 1;

        TrainingTemplateLesson trainingTemplateLesson = trainingTemplateLessonRepository.findById(dto.getTemplateLessonId()).orElseThrow();

        List<LessonTask> lessonTasks = trainingTemplateLesson.getLesson().getLessonTasks();

        if (lessonTasks.size() > 0) {
            counter = lessonTasks.get(lessonTasks.size() - 1).getNumber() + 1;
        }

        for (long tasksId : dto.getTaskIds()) {
            LessonTask lessonTask = new LessonTask();
            lessonTask.setLesson(trainingTemplateLesson.getLesson());
            Task task = taskService.findByIdOrThrow(tasksId);
            lessonTask.setTask(task);
            lessonTask.setNumber(counter++);
            lessonTask.setDeadlineDays(dto.getDeadlineDays());
            lessonTask.setDeadlineHour(dto.getDeadlineHour());
            lessonTask.setMandatory(dto.getMandatoryTopicIds().contains(tasksId));
            lessonTaskRepository.save(lessonTask);

            taskService.save(task);
        }
    }

    public void delete(LessonTask lessonTask) {
        lessonTaskRepository.delete(lessonTask);
    }

    public void saveUserTask(UserTask userTask) {
        userTaskRepository.save(userTask);
    }
}
