package pl.javastart.bootcamp.domain.admin.template;

import lombok.Data;

@Data
public class EditExerciseInTemplateLessonDto {

    private Long lessonExerciseId;
    private Long templateId;
    private Long templateLessonId;

    private Long number;

}
