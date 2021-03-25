package pl.javastart.bootcamp.domain.admin.training;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.agreement.company.CompanyRepository;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupStatus;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingFirstDateComparator;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.training.description.TrainingDescriptionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/admin/szkolenia")
@Controller
public class AdminTrainingController {

    private final TrainingService trainingService;
    private final TrainingDescriptionService trainingDescriptionService;
    private final CompanyRepository companyRepository;

    public AdminTrainingController(TrainingService trainingService,
                                   TrainingDescriptionService trainingDescriptionService,
                                   CompanyRepository companyRepository) {
        this.trainingService = trainingService;
        this.trainingDescriptionService = trainingDescriptionService;
        this.companyRepository = companyRepository;
    }

    @GetMapping("")
    public String trainings(Model model, @RequestParam(required = false, defaultValue = "false") Boolean showAll) {
        List<Training> allTrainings = trainingService.findAll();
        List<TrainingVm> trainings = allTrainings
                .stream()
                .filter(t -> showAll || t.getStatus().isActive())
                .sorted(new TrainingFirstDateComparator())
                .map(TrainingVm::toVm)
                .collect(Collectors.toList());
        model.addAttribute("trainings", trainings);
        return "admin/trainings";
    }


    @GetMapping("/{id}")
    public String training(@PathVariable Long id,
                           @RequestParam(value = "odrzucone", defaultValue = "false", required = false) Boolean rejected,
                           Model model) {
        Optional<Training> trainingOptional = trainingService.findById(id);
        if (trainingOptional.isPresent()) {
            Training training = trainingOptional.get();
            model.addAttribute("training", training);
            List<Signup> signups = training.getSignups();
            if (!rejected) {
                signups = training.getSignups().stream().filter(signup -> signup.getStatus() != SignupStatus.REJECTED).collect(Collectors.toList());
            }
            model.addAttribute("signups", signups);
            String concatenatedUserMails = signups.stream().map(s -> s.getUser().getEmail())
                    .collect(Collectors.joining(";"));
            model.addAttribute("concatenatedUserMails", concatenatedUserMails);

            return "admin/training";
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @GetMapping("/dodaj")
    public String addTraining(Model model, @RequestParam(required = false) Long copyFromId) {
        Training training = new Training();
        training.setCompany(companyRepository.findById(1L).orElse(null));
        training.setDeposit(BigDecimal.valueOf(1000));
        training.setPrice(BigDecimal.valueOf(5000));
        if (copyFromId != null) {
            Optional<Training> baseTrainingOptional = trainingService.findById(copyFromId);
            if (baseTrainingOptional.isPresent()) {
                training = baseTrainingOptional.get();
                training.setId(null);
                training.setCode(training.getCode() + "-copy");
            }
        }

        model.addAttribute("training", training);
        model.addAttribute("descriptions", trainingDescriptionService.findAll());
        model.addAttribute("companies", companyRepository.findAll());
        return "admin/editTraining";
    }

    @PostMapping("/dodaj")
    public String addTraining(Training training) {
        trainingService.insert(training);
        return "redirect:/admin/szkolenia/" + training.getId();
    }

    @GetMapping("/{id}/edytuj")
    public String editTraining(@PathVariable Long id, Model model) {
        Optional<Training> trainingOptional = trainingService.findById(id);
        if (trainingOptional.isPresent()) {
            model.addAttribute("training", trainingOptional.get());
            model.addAttribute("descriptions", trainingDescriptionService.findAll());
            model.addAttribute("companies", companyRepository.findAll());
            return "admin/editTraining";
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PostMapping("/edytuj")
    public String updateTraining(Training training) {
        trainingService.update(training);
        return "redirect:/admin/szkolenia/" + training.getId();
    }
}
