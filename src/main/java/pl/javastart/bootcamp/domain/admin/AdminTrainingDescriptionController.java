package pl.javastart.bootcamp.domain.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.training.description.TrainingDescription;
import pl.javastart.bootcamp.domain.training.description.TrainingDescriptionService;

import java.util.Optional;

@RequestMapping("/admin/opisy")
@Controller
public class AdminTrainingDescriptionController {

    private TrainingDescriptionService trainingDescriptionService;

    public AdminTrainingDescriptionController(TrainingDescriptionService trainingDescriptionService) {
        this.trainingDescriptionService = trainingDescriptionService;
    }

    @GetMapping("")
    public String descriptions(Model model) {
        model.addAttribute("descriptions", trainingDescriptionService.findAll());
        return "admin/descriptions";
    }

    @GetMapping("/dodaj")
    public String addDescription(Model model) {
        model.addAttribute("description", new TrainingDescription());
        return "admin/editDescription";
    }

    @PostMapping("/dodaj")
    public String addDescription(TrainingDescription trainingDescription) {
        trainingDescriptionService.insert(trainingDescription);
        return "redirect:/admin/opisy";
    }

    @GetMapping("/{id}/edytuj")
    public String editDescription(@PathVariable Long id, Model model) {
        Optional<TrainingDescription> descriptionOptional = trainingDescriptionService.findById(id);
        if (descriptionOptional.isPresent()) {
            model.addAttribute("description", descriptionOptional.get());
            return "admin/editDescription";
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PostMapping("/edytuj")
    public String editDescription(TrainingDescription trainingDescription) {
        trainingDescriptionService.update(trainingDescription);
        return "redirect:/admin/opisy";
    }


}
