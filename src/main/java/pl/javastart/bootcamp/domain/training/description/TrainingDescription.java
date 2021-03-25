package pl.javastart.bootcamp.domain.training.description;

import lombok.Getter;
import lombok.Setter;
import pl.javastart.bootcamp.domain.training.Training;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class TrainingDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String url;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String contentShort;

    @Column(columnDefinition = "TEXT")
    private String extraService;

    @OneToMany(mappedBy = "description")
    private List<Training> trainings;

    private boolean noindex;

    private String metaDescription;

}
