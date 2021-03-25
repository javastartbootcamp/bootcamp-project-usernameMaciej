package pl.javastart.bootcamp.domain.training.lesson.lessonexcercise;

import lombok.Getter;
import lombok.Setter;
import pl.javastart.bootcamp.domain.admin.task.Task;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class LessonExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long number;

    @ManyToOne
    private Lesson lesson;

    @ManyToOne
    private Task task;

}
