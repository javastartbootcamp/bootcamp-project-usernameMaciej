package pl.javastart.bootcamp.domain.admin.template;

import lombok.Data;

import java.util.Set;

@Data
public class AddExerciseToTemplateLessonDto {

    private Long templateId;
    private Long templateLessonId;

    private Set<Long> taskIds;

}
