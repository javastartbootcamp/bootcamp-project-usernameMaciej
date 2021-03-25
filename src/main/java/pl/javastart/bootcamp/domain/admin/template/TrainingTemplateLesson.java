package pl.javastart.bootcamp.domain.admin.template;

import lombok.Getter;
import lombok.Setter;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class TrainingTemplateLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Lesson lesson;

    @ManyToOne
    private TrainingTemplate trainingTemplate;

    private Long number;

}
