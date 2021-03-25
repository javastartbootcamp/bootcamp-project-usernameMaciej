package pl.javastart.bootcamp.domain.user.training.lesson.task;

import lombok.Data;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;

import java.time.LocalDateTime;

@Data
public class TaskWithResultDto {
    private LessonTask lessonTask;
    private String points;
    private LocalDateTime deadline;
}
