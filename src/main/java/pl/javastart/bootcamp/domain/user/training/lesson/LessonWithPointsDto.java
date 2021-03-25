package pl.javastart.bootcamp.domain.user.training.lesson;

import lombok.Data;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;

import java.math.BigDecimal;

@Data
public class LessonWithPointsDto {

    private Lesson lesson;
    private BigDecimal points;
    private String pointsFormatted;
    private int maxPoints;
    private boolean visible;

    private BigDecimal additionalPoints;
    private String additionalPointsFormatted;
    private int maxAdditionalPoints;
}
