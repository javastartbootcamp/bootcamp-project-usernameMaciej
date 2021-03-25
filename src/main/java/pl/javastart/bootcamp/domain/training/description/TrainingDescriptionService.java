package pl.javastart.bootcamp.domain.training.description;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingDescriptionService {

    private TrainingDescriptionRepository trainingDescriptionRepository;

    public TrainingDescriptionService(TrainingDescriptionRepository trainingDescriptionRepository) {
        this.trainingDescriptionRepository = trainingDescriptionRepository;
    }

    public Optional<TrainingDescription> findByUrl(String url) {
        return trainingDescriptionRepository.findByUrl(url);
    }

    public List<TrainingDescription> findAll() {
        return trainingDescriptionRepository.findAll();
    }

    public Optional<TrainingDescription> findById(Long id) {
        return trainingDescriptionRepository.findById(id);
    }

    public TrainingDescription insert(TrainingDescription trainingDescription) {
        if (trainingDescription.getId() != null) {
            throw new IllegalArgumentException("ID should be null when adding");
        }
        return trainingDescriptionRepository.save(trainingDescription);
    }

    public TrainingDescription update(TrainingDescription trainingDescription) {
        if (trainingDescription.getId() == null) {
            throw new IllegalArgumentException("ID should be not be null when updating");
        }
        return trainingDescriptionRepository.save(trainingDescription);
    }
}
