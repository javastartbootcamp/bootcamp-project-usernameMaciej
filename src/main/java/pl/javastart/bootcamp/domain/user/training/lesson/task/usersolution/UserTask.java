package pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution;

import lombok.Getter;
import lombok.Setter;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class UserTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private LessonTask lessonTask;

    private BigDecimal points;

    @Column(columnDefinition = "TEXT")
    private String description;

    private ZonedDateTime startedAt;

    private String solutionUrl;

    private Boolean toBeChecked = false;

    private LocalDateTime deadline;

    @OrderBy("dateTime asc")
    @OneToMany(mappedBy = "userTask")
    private List<UserTaskEntry> entries;

}
