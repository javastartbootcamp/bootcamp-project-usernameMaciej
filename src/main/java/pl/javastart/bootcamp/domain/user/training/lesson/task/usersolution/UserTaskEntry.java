package pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class UserTaskEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserTask userTask;

    private String text;

    private ZonedDateTime dateTime;

}
