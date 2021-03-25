package pl.javastart.bootcamp.domain.admin.task.rating;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.JavaStartProperties;
import pl.javastart.bootcamp.domain.admin.task.Task;
import pl.javastart.bootcamp.domain.admin.task.TaskRepository;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.mail.MailService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TaskRatingService {

    private final TaskRatingRepository taskRatingRepository;
    private final MailService mailService;
    private final TaskRepository taskRepository;
    private final JavaStartProperties javaStartProperties;

    public TaskRatingService(TaskRatingRepository taskRatingRepository,
                             MailService mailService,
                             TaskRepository taskRepository,
                             JavaStartProperties javaStartProperties) {
        this.taskRatingRepository = taskRatingRepository;
        this.mailService = mailService;
        this.taskRepository = taskRepository;
        this.javaStartProperties = javaStartProperties;
    }

    public TaskRatingDto findByUserAndTaskIdOrElseNew(User user, Task task) {
        return taskRatingRepository.findByUserAndTaskId(user, task.getId())
                .map(taskRating -> new TaskRatingDto(taskRating.getTask().getId(), taskRating.getRating(), taskRating.getComment()))
                .orElseGet(() -> new TaskRatingDto(task.getId(), 0, null));
    }

    public void createOrUpdateRating(TaskRatingDto dto, User user) {
        Optional<TaskRating> currentRatingOptional = taskRatingRepository.findByUserAndTaskId(user, dto.getTaskId());

        TaskRating taskRating = currentRatingOptional.orElseGet(() -> {
            TaskRating newRating = new TaskRating();
            newRating.setCreatedAt(LocalDateTime.now());
            newRating.setUser(user);
            return newRating;
        });

        String previousRating = null;

        if (currentRatingOptional.isPresent()) {
            TaskRating currentTaskRating = currentRatingOptional.get();
            previousRating = "<p>Poprzednia ocena: " + currentTaskRating.getRating() + "/5, <br><br>Komentarz (co możemy poprawić?):<br> " + currentTaskRating.getComment() + "</p>";
        }

        Task task = taskRepository.findById(dto.getTaskId()).orElseThrow();

        taskRating.setRating(dto.getRating());
        taskRating.setTask(task);
        taskRating.setComment(dto.getComment());

        taskRatingRepository.save(taskRating);

        sendEmailNotificationToAdmin(task, user, previousRating, taskRating);
    }

    private void sendEmailNotificationToAdmin(Task task, User user, String previousRating, TaskRating taskRating) {
        String url = javaStartProperties.getFullDomainAddress() + "/admin/zadania/" + task.getId();
        String taskLink = "<a href=\"" + url + "\">" + task.getName() + "</a>";

        String message = String.format("<p>%s %s ocenił(a) zadanie %s</p>", user.getFirstName(), user.getLastName(), taskLink);

        message += "<p>Ocena: " + taskRating.getRating() + "/5, <br><br>Komentarz (co możemy poprawić?):<br>" + taskRating.getComment() + "</p>";

        if (previousRating != null) {
            message += previousRating;
        }

        mailService.sendTaskRatingEmail(message);
    }
}
