package pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class UserTaskWithLastEntryDto {

    private String username;
    private Long lessonTaskId;
    private Long userId;
    private String taskNumberAndName;
    private ZonedDateTime lastEntryDate;
}
