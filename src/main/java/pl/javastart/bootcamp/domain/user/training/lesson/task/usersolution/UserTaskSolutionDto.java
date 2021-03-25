package pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution;

import lombok.Data;

@Data
public class UserTaskSolutionDto {

    private Long userTaskId;
    private Long lessonTaskId;
    private String url;
}
