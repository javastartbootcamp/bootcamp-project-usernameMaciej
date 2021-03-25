package pl.javastart.bootcamp.domain.admin.template;

import lombok.Data;

@Data
public class TrainingTemplateLessonDto {

    private Long id;
    private Long trainingTemplateId;

    private String title;
    private Long number;
    private String videoLinks;
    private String lessonLinks;
    private Long lessonId;

}
