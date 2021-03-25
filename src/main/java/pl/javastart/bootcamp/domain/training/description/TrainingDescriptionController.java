package pl.javastart.bootcamp.domain.training.description;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.training.TrainingStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class TrainingDescriptionController {

    private TrainingDescriptionService trainingDescriptionService;
    private TrainingService trainingService;

    public TrainingDescriptionController(TrainingDescriptionService trainingDescriptionService, TrainingService trainingService) {
        this.trainingDescriptionService = trainingDescriptionService;
        this.trainingService = trainingService;
    }

    @GetMapping("/szkolenie/{url}")
    public String training(@PathVariable String url, Model model) {
        Optional<TrainingDescription> trainingDescriptionOptional = trainingDescriptionService.findByUrl(url);

        if (!trainingDescriptionOptional.isPresent()) {
            throw new ResourceNotFoundException();

        }
        TrainingDescription trainingDescription = trainingDescriptionOptional.get();
        List<Training> trainings = trainingDescription.getTrainings()
                .stream()
                .filter(training -> training.getStatus() == TrainingStatus.PLANNED)
                .collect(Collectors.toList());
        model.addAttribute("trainings", trainings);
        model.addAttribute("trainingDescription", trainingDescription);

        Long nextTrainingId = trainingService.findNextPlannedTrainingIdForGivenUrl(url).orElse(null);
        model.addAttribute("nextTrainingId", nextTrainingId);
        model.addAttribute("nextTrainingUrl", url);

        return "trainingDescription";
    }
}
