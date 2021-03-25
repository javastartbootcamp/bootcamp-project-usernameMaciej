package pl.javastart.bootcamp.domain.training.lesson;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExercise;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    private Training training;

    private String title;

    private String linkToSlack;

    private Long number;

    @Column(columnDefinition = "TEXT")
    private String videoLinks;

    @Column(columnDefinition = "TEXT")
    private String lessonLinks;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lessonDate;

    @OneToMany(mappedBy = "lesson")
    @OrderBy("number")
    private List<LessonTask> lessonTasks;

    @OneToMany(mappedBy = "lesson")
    @OrderBy("number")
    private List<LessonExercise> lessonExercises;

    private Long sortOrder;

    private boolean visible;

}


