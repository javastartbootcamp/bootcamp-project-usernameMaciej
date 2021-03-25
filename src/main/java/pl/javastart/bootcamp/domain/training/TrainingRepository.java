package pl.javastart.bootcamp.domain.training;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByStatusAndCategory(TrainingStatus status, TrainingCategory category);

    List<Training> findByStatusAndDescription_Url(TrainingStatus status, String trainingDescriptionUrl);
}
