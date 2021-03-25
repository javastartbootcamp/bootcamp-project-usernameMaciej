package pl.javastart.bootcamp.domain.admin.training.rating;

import lombok.Data;

import java.util.List;

@Data
public class TaskWithRatingWrapperDto {

    private Long userId;
    private Long trainingId;
    private List<TaskWithRatingDto> tasksWithRating;


}
