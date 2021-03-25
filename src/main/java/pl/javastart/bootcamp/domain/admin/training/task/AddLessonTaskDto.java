package pl.javastart.bootcamp.domain.admin.training.task;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class AddLessonTaskDto {

    private Long lessonId;

    private Set<Long> taskIds;
    private Set<Long> mandatoryTopicIds;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;

}
