package pl.javastart.bootcamp.domain.admin.task.rating;

import lombok.Getter;
import lombok.Setter;
import pl.javastart.bootcamp.domain.admin.task.Task;
import pl.javastart.bootcamp.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TaskRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Task task;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    private User user;

    private LocalDateTime createdAt;

}
