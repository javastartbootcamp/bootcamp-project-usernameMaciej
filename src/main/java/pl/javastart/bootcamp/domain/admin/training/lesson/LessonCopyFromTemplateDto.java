package pl.javastart.bootcamp.domain.admin.training.lesson;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Data
public class LessonCopyFromTemplateDto {

    private Long lessonId;

    private String title;

    private Long sortOrder;

    private Long number;

    private String linkToSlack;

    private Long trainingId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lessonDate;

    private Set<Long> topicIds;

    private Long templateId;
    private Long templateLessonId;
}
