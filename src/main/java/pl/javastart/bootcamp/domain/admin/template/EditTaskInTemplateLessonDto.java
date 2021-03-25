package pl.javastart.bootcamp.domain.admin.template;

import lombok.Data;

import java.time.LocalTime;

@Data
public class EditTaskInTemplateLessonDto {

    private Long lessonTaskId;
    private Long templateId;
    private Long templateLessonId;

    private Long number;
    private boolean isMandatory;

    private Integer deadlineDays;
    private LocalTime deadlineHour;

}
