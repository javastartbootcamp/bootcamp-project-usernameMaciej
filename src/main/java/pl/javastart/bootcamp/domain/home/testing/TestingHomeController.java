package pl.javastart.bootcamp.domain.home.testing;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingCategory;
import pl.javastart.bootcamp.domain.training.TrainingFirstDateComparator;
import pl.javastart.bootcamp.domain.training.TrainingService;

import java.util.List;

@Controller
public class TestingHomeController {

    private TrainingService trainingService;

    public TestingHomeController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/szkolenie-testowanie")
    public String home(Model model) {
        List<Training> trainings = trainingService.findPlannedByCategory(TrainingCategory.TESTER);
        trainings.sort(new TrainingFirstDateComparator());
        model.addAttribute("trainings", trainings);
        return "testingHome";
    }
}
