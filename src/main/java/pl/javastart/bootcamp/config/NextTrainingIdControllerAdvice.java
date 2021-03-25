package pl.javastart.bootcamp.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.javastart.bootcamp.domain.training.TrainingService;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class NextTrainingIdControllerAdvice {

    private TrainingService trainingService;

    public NextTrainingIdControllerAdvice(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @ModelAttribute("nextTrainingId")
    public Long nextTrainingId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean isHomepage = uri.equals("/");
        boolean isTrainingDescription = uri.startsWith("/szkolenie/") && !uri.matches(".*[0-9]+");
        if (isHomepage || isTrainingDescription) {
            return trainingService.findNextPlannedTrainingIdForGivenUrl("junior-java-developer-online").orElse(null);
        }
        return null;
    }

    @ModelAttribute("nextTrainingUrl")
    public String nextTrainingUrl() {
        return "junior-java-developer-online";
    }
}
