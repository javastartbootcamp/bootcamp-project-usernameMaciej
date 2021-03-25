package pl.javastart.bootcamp.domain.admin.template;

import lombok.Data;

import java.time.LocalTime;
import java.util.Set;

@Data
public class AddTaskToTemplateLessonDto {

    private Long templateId;
    private Long templateLessonId;

    private Set<Long> taskIds;
    private Set<Long> mandatoryTopicIds;

    private Integer deadlineDays;
    private LocalTime deadlineHour;

}
