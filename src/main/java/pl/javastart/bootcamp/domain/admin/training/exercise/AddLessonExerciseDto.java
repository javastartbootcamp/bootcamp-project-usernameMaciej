package pl.javastart.bootcamp.domain.admin.training.exercise;

import lombok.Data;

import java.util.Set;

@Data
public class AddLessonExerciseDto {

    private Long lessonId;

    private Set<Long> taskIds;

    private Long templateId;

    private Long templateLessonId;

}
