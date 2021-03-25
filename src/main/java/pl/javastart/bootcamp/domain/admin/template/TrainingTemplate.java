package pl.javastart.bootcamp.domain.admin.template;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class TrainingTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "trainingTemplate")
    @OrderBy("number")
    private List<TrainingTemplateLesson> lessons;

}
