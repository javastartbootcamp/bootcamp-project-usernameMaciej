package pl.javastart.bootcamp.domain.user.training;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.domain.training.lesson.LessonService;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.training.lesson.LessonWithPointsDto;
import pl.javastart.bootcamp.utils.BigDecimalFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class UserTrainingService {

    private BigDecimalFormatter bigDecimalFormatter;
    private LessonService lessonService;

    public UserTrainingService(BigDecimalFormatter bigDecimalFormatter, LessonService lessonService) {
        this.bigDecimalFormatter = bigDecimalFormatter;
        this.lessonService = lessonService;
    }

    public UserTrainingResultDto calculateResult(Long trainingId, User user) {

        List<LessonWithPointsDto> lessons = lessonService.findLessonsForTrainingWithUser(trainingId, user);

        int maxPoints = lessons.stream()
                .flatMap(dto -> dto.getLesson().getLessonTasks().stream().filter(LessonTask::isMandatory))
                .mapToInt(lt -> lt.getTask().getPoints())
                .sum();
        BigDecimal points = lessons.stream()
                .map(LessonWithPointsDto::getPoints)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal additionalPoints = lessons.stream().map(LessonWithPointsDto::getAdditionalPoints).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pointsSum = points.add(additionalPoints);

        BigDecimal percents = BigDecimal.ZERO;
        if (maxPoints > 0) {
            percents = pointsSum.divide(BigDecimal.valueOf(maxPoints), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        UserTrainingResultDto dto = new UserTrainingResultDto();

        dto.setPercentage(bigDecimalFormatter.convertDecimalToString(percents));
        dto.setMaxPoints(maxPoints);
        dto.setPoints(bigDecimalFormatter.convertDecimalToString(pointsSum));

        return dto;
    }


}
