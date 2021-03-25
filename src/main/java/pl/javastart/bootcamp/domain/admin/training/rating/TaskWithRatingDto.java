package pl.javastart.bootcamp.domain.admin.training.rating;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskWithRatingDto {

    private Long userTaskId;
    private Long taskId;
    private String taskName;
    private BigDecimal points;
    private boolean mandatory;
    private int maxPoints;
    private String solutionUrl;
}
