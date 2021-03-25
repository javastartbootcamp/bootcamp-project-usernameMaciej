package pl.javastart.bootcamp.domain.admin.training.task;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.javastart.bootcamp.domain.github.GithubService;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskRepository;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTask;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTaskRepository;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTaskSolutionService;

import java.time.ZonedDateTime;

@RestController
public class LessonTaskResource {

    private final GithubService githubService;
    private final LessonTaskRepository lessonTaskRepository;
    private final LessonTaskService lessonTaskService;
    private final UserService userService;
    private final UserTaskRepository userTaskRepository;
    private final UserTaskSolutionService userTaskSolutionService;

    public LessonTaskResource(GithubService githubService,
                              LessonTaskRepository lessonTaskRepository,
                              LessonTaskService lessonTaskService,
                              UserService userService,
                              UserTaskRepository userTaskRepository,
                              UserTaskSolutionService userTaskSolutionService) {
        this.githubService = githubService;
        this.lessonTaskRepository = lessonTaskRepository;
        this.lessonTaskService = lessonTaskService;
        this.userService = userService;
        this.userTaskRepository = userTaskRepository;
        this.userTaskSolutionService = userTaskSolutionService;
    }

    @PostMapping("/api/lesson-task/{lessonTaskId}/start")
    public void startTask(@PathVariable Long lessonTaskId) {
        LessonTask lessonTask = lessonTaskRepository.findById(lessonTaskId).orElseThrow();

        User currentUser = userService.getCurrentUser();
        UserTask userTask = lessonTaskService
                .findUserTaskByLessonTaskIdAndUserId(lessonTaskId, currentUser.getId())
                .orElseGet(() -> {
                            UserTask ut = new UserTask();
                            ut.setUser(currentUser);
                            ut.setLessonTask(lessonTask);
                            ut.setToBeChecked(false);
                            ut.setDeadline(lessonTask.getDeadline());
                            ut.setStartedAt(ZonedDateTime.now());
                            return ut;
                        }
                );

        String githubUsername = currentUser.getGithubUsername();

        String baseRepositoryUrl = lessonTask.getTask().getBaseRepositoryUrl();
        String trainingCode = lessonTask.getLesson().getTraining().getCode();
        String repoName = trainingCode + "_" + githubUsername + "_zad_" + lessonTask.getLesson().getNumber() + "." + lessonTask.getNumber();

        String trainersGithubUsernames = lessonTask.getLesson().getTraining().getTrainersGithubUsernames();
        String[] trainerUsernames = trainersGithubUsernames.split(",");

        String createdRepositoryUrl = githubService.cloneRepository(baseRepositoryUrl, githubUsername, repoName, trainerUsernames);

        userTask.setSolutionUrl(createdRepositoryUrl);
        userTaskRepository.save(userTask);

        userTaskSolutionService.userStartedSolving(userTask);
    }

}
