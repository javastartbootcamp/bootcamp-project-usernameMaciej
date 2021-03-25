package pl.javastart.bootcamp.domain.admin.task;

import lombok.Getter;
import lombok.Setter;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExercise;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.utils.OrderableItem;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Task implements OrderableItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String tags;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int points;

    private String baseRepositoryUrl;

    private String solutionVideo;

    private String solutionSourceUrl;

    private Long sortOrder;

    @OneToMany(mappedBy = "task")
    private List<LessonExercise> lessonExercises;

    @OneToMany(mappedBy = "task")
    private List<LessonTask> lessonTasks;

    private boolean archived;

    private LocalDate lastUsed;

}
