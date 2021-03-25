package pl.javastart.bootcamp.domain.training;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.javastart.bootcamp.domain.agreement.company.Company;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.training.description.TrainingDescription;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal deposit;
    private BigDecimal price;
    private BigDecimal regularPrice;
    private String type;
    private String code;
    private int minAttendees;
    private int maxAttendees;

    private LocalTime hourFrom;
    private LocalTime hourTo;

    @Column(columnDefinition = "TEXT")
    private String hoursDescription;

    @Column(columnDefinition = "TEXT")
    private String dates;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fullPaymentFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fullPaymentTo;

    @ManyToOne
    private TrainingDescription description;

    @OneToMany(mappedBy = "training")
    @OrderBy("id")
    private List<Signup> signups;

    @Enumerated(EnumType.STRING)
    private TrainingStatus status = TrainingStatus.PLANNED;

    @OneToMany(mappedBy = "training")
    @OrderBy("number")
    private List<Lesson> lessons;

    @ManyToOne
    private Company company;

    @Enumerated(EnumType.STRING)
    private TrainingCategory category;

    private String slackBotAccessToken;

    private String trainersGithubUsernames;

}
