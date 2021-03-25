package pl.javastart.bootcamp.domain.admin.training.rating;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTask;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTaskSolutionService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private final LessonTaskService lessonTaskService;
    private final UserService userService;
    private final UserTaskSolutionService userTaskSolutionService;

    public RatingService(LessonTaskService lessonTaskService,
                         UserService userService,
                         UserTaskSolutionService userTaskSolutionService) {
        this.lessonTaskService = lessonTaskService;
        this.userService = userService;
        this.userTaskSolutionService = userTaskSolutionService;
    }

    public TaskWithRatingWrapperDto findTasksWithRatings(User user, Training training) {

        Map<LessonTask, List<UserTask>> userTasks = lessonTaskService.getTaskListMap(user);

        TaskWithRatingWrapperDto dto = new TaskWithRatingWrapperDto();

        List<LessonTask> lessonTasks = training.getLessons()
                .stream()
                .filter(Lesson::isVisible)
                .flatMap(lesson -> lesson.getLessonTasks().stream())
                .collect(Collectors.toList());

        List<TaskWithRatingDto> taskWithRatingDtos = new LinkedList<>();

        for (LessonTask lessonTask : lessonTasks) {
            TaskWithRatingDto taskWithRatingDto = new TaskWithRatingDto();
            taskWithRatingDto.setTaskId(lessonTask.getTask().getId());
            taskWithRatingDto.setUserTaskId(lessonTask.getId());
            taskWithRatingDto.setTaskName(lessonTask.getTask().getName());
            taskWithRatingDto.setMandatory(lessonTask.isMandatory());
            taskWithRatingDto.setMaxPoints(lessonTask.getTask().getPoints());

            if (userTasks.containsKey(lessonTask)) {
                UserTask userTask = userTasks.get(lessonTask).get(0);
                taskWithRatingDto.setSolutionUrl(userTask.getSolutionUrl());
                taskWithRatingDto.setPoints(userTask.getPoints());
            }

            taskWithRatingDtos.add(taskWithRatingDto);
        }

        dto.setTrainingId(training.getId());
        dto.setUserId(user.getId());
        dto.setTasksWithRating(taskWithRatingDtos);

        return dto;
    }

    public void save(TaskWithRatingWrapperDto wrapper) {
        User user = userService.findByIdOrThrow(wrapper.getUserId());
        for (TaskWithRatingDto taskWithRating : wrapper.getTasksWithRating()) {
            Optional<UserTask> userTaskOptional = lessonTaskService.findUserTaskByLessonTaskIdAndUserId(taskWithRating.getUserTaskId(), wrapper.getUserId());
            if (userTaskOptional.isPresent()) {
                UserTask userTask = userTaskOptional.get();

                if (!Objects.equals(userTask.getPoints(), taskWithRating.getPoints())
                        || !Objects.equals(userTask.getSolutionUrl(), taskWithRating.getSolutionUrl())) {
                    userTask.setPoints(taskWithRating.getPoints());
                    userTask.setSolutionUrl(taskWithRating.getSolutionUrl());
                    userTaskSolutionService.handleRatingSendByTrainer(userTask);
                }
            } else {
                UserTask userTask = new UserTask();
                userTask.setPoints(taskWithRating.getPoints());
                userTask.setSolutionUrl(taskWithRating.getSolutionUrl());
                userTask.setUser(user);
                LessonTask lessonTask = lessonTaskService.findByIdOrThrow(taskWithRating.getUserTaskId());
                userTask.setLessonTask(lessonTask);
                userTask.setDeadline(lessonTask.getDeadline());
                userTaskSolutionService.handleRatingSendByTrainer(userTask);
            }
        }
    }
}
