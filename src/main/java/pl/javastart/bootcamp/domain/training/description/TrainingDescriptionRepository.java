package pl.javastart.bootcamp.domain.training.description;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingDescriptionRepository extends JpaRepository<TrainingDescription, Long> {
    Optional<TrainingDescription> findByUrl(String url);
}
