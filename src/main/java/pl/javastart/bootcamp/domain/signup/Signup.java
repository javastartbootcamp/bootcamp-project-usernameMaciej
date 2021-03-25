package pl.javastart.bootcamp.domain.signup;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.javastart.bootcamp.domain.signup.log.SignupLogItem;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
public class Signup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Training training;

    @Enumerated(EnumType.STRING)
    private SignupStatus status;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String financingMethod;

    private LocalDateTime signupDate;

    @OneToMany(mappedBy = "signup", cascade = CascadeType.REMOVE)
    @OrderBy("itemDate DESC")
    private List<SignupLogItem> logItems;

    private LocalDateTime approvedDate;

    private LocalDateTime agreementSigningDate;
    private String agreementSigningIp;
    private String agreementTemplateFileName;
    private String agreementSignedFileName;
    private BigDecimal deposit;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private SignupNotificationStatus notificationStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate advancePaymentTo;

    private LocalDate advancePaymentToOverride;
    private LocalDate fullPaymentFromOverride;
    private LocalDate fullPaymentToOverride;

    private boolean canSeeContent;

    private String slackChannelId;

    private int homeworkExtensionsLeft;

    private Integer lessonTo;

    private String customPaymentInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Signup signup = (Signup) o;
        return Objects.equals(id, signup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
