package pl.javastart.bootcamp.domain.user.training;

import lombok.Data;

@Data
public class UserTrainingResultDto {

    private String points;
    private String percentage;
    private int maxPoints;
}
