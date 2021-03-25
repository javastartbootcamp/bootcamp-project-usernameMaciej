package pl.javastart.bootcamp.domain.admin.task.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskRatingDto {

    private Long taskId;

    private int rating;

    private String comment;

}
