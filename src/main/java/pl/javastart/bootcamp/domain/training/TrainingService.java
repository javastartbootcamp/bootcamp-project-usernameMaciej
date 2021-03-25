package pl.javastart.bootcamp.domain.training;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.admin.training.exercise.AddLessonExerciseDto;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingService {

    private TrainingRepository trainingRepository;

    public TrainingService(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public List<Training> findAll() {
        return trainingRepository.findAll();
    }

    public List<Training> findPlannedByCategory(TrainingCategory trainingCategory) {
        return trainingRepository.findByStatusAndCategory(TrainingStatus.PLANNED, trainingCategory);
    }

    public Optional<Long> findNextPlannedTrainingIdForGivenUrl(String descriptionUrl) {
        return trainingRepository.findByStatusAndDescription_Url(TrainingStatus.PLANNED, descriptionUrl)
                .stream()
                .sorted(this::compareTrainingsByDate)
                .map(Training::getId)
                .findFirst();
    }

    private int compareTrainingsByDate(Training t1, Training t2) {
        List<LocalDate> dates1 = parseDatesToList(t1.getDates());
        List<LocalDate> dates2 = parseDatesToList(t2.getDates());
        if (dates1.size() > 0 && dates2.size() > 0) {
            LocalDate date1 = dates1.get(0);
            LocalDate date2 = dates2.get(0);
            return date1.compareTo(date2);
        }
        if (dates1.size() == 0 && dates2.size() == 0) {
            return 0;
        }
        if (dates1.size() == 0) {
            return -1;
        }
        return 1;
    }

    public List<Training> findAllActive() {
        return trainingRepository.findAll().stream().filter(t -> t.getStatus().isActive()).collect(Collectors.toList());
    }

    public List<Training> findAllInProgress() {
        return trainingRepository.findAll().stream().filter(t -> t.getStatus() == TrainingStatus.IN_PROGRESS).collect(Collectors.toList());
    }

    public Optional<Training> findById(Long id) {
        return trainingRepository.findById(id);
    }

    public Training findByIdOrThrow(Long id) {
        return trainingRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public Training insert(Training training) {
        if (training.getId() != null) {
            throw new IllegalArgumentException("ID should be null when adding");
        }
        return trainingRepository.save(training);
    }

    public Training update(Training training) {
        if (training.getId() == null) {
            throw new IllegalArgumentException("ID should be not be null when updating");
        }
        Optional<Training> existingTraining = findById(training.getId());
        if (existingTraining.isPresent()) {
            String codeBefore = existingTraining.get().getCode();
        }
        if (training.getStatus() != TrainingStatus.CANCELLED) {
            List<LocalDate> dates = parseDatesToList(training.getDates());
        }

        return trainingRepository.save(training);
    }

    private List<LocalDate> parseDatesToList(String dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return Arrays.stream(dates.split(", ")).map(string -> LocalDate.parse(string, formatter)).collect(Collectors.toList());
    }

    public List<User> findAllParticipants(Training training) {
        return training.getSignups().stream()
                .filter(Signup::isCanSeeContent)
                .map(Signup::getUser)
                .sorted(Comparator.comparing(User::getLastName))
                .collect(Collectors.toList());
    }


    public void save(AddLessonExerciseDto addLessonExerciseDto) {
    }
}
