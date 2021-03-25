package pl.javastart.bootcamp.domain.admin.training.lesson;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplate;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplateLesson;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplateService;
import pl.javastart.bootcamp.domain.admin.topic.TopicService;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;
import pl.javastart.bootcamp.domain.training.lesson.LessonService;

import java.util.List;

@Controller
public class AdminLessonController {

    private final TrainingService trainingService;
    private final LessonService lessonService;
    private final TopicService topicService;
    private final TrainingTemplateService trainingTemplateService;

    public AdminLessonController(TrainingService trainingService,
                                 LessonService lessonService,
                                 TopicService topicService,
                                 TrainingTemplateService trainingTemplateService) {
        this.trainingService = trainingService;
        this.lessonService = lessonService;
        this.topicService = topicService;
        this.trainingTemplateService = trainingTemplateService;
    }

    @GetMapping("/admin/szkolenia/{trainingId}/lekcje")
    public String lessons(@PathVariable Long trainingId, Model model) {
        Training training = trainingService.findByIdOrThrow(trainingId);
        model.addAttribute("training", training);
        return "admin/lesson/lessons";
    }

    @GetMapping("/admin/lekcje/{lessonId}")
    public String lesson(@PathVariable Long lessonId, Model model) {
        Lesson lesson = lessonService.findByIdOrThrow(lessonId);
        model.addAttribute("lesson", lesson);
        return "admin/lesson/lesson";
    }

    @GetMapping("/admin/lekcje/dodaj")
    public String addLessonForm(@RequestParam Long trainingId, Model model) {
        LessonAddDto lesson = lessonService.prepareLessonAddDto(trainingId);
        model.addAttribute("lesson", lesson);
        model.addAttribute("topics", topicService.findAll());
        return "admin/lesson/lessonAdd";
    }

    @PostMapping("/admin/lekcje/dodaj")
    public String addLesson(LessonAddDto addLessonDto) {
        Lesson lesson = lessonService.save(addLessonDto);
        return "redirect:/admin/lekcje/" + lesson.getId();
    }

    @PostMapping("/admin/lekcje/dodajTematy")
    public String addTopicsToLesson(LessonAddDto dto) {
        Lesson lesson = lessonService.addTopicsToLesson(dto);
        if (dto.getTemplateId() != null && dto.getTemplateLessonId() != null) {
            return String.format("redirect:/admin/szablony/%d/lekcje/%d", dto.getTemplateId(), dto.getTemplateLessonId());
        } else {
            return "redirect:/admin/lekcje/" + lesson.getId();
        }
    }

    @GetMapping("/admin/lekcje/{id}/edytuj")
    public String editLessonForm(@PathVariable Long id, Model model) {
        Lesson lesson = lessonService.findByIdOrThrow(id);
        model.addAttribute("lesson", lesson);
        model.addAttribute("mode", "edit");
        model.addAttribute("topics", topicService.findAll());
        return "admin/lesson/lessonEdit";
    }

    @PostMapping("/admin/lekcje/edytuj")
    public String saveLesson(Lesson lesson) {
        lessonService.save(lesson);
        return "redirect:/admin/szkolenia/" + lesson.getTraining().getId() + "/lekcje";
    }

    @GetMapping("/admin/lekcje/{id}/widocznosc")
    public String toggleVisibility(@PathVariable Long id, @RequestParam(name = "pokaz") boolean visible) {
        Lesson lesson = lessonService.findByIdOrThrow(id);
        lesson.setVisible(visible);
        lessonService.save(lesson);
        return "redirect:/admin/szkolenia/" + lesson.getTraining().getId() + "/lekcje";
    }

    @GetMapping("/admin/lekcje/kopiuj")
    public String copyLessonFromTemplateFrom(@RequestParam Long trainingId, Model model) {
        LessonCopyFromTemplateDto lesson = lessonService.prepareLessonCopyFromTemplateDto(trainingId);
        model.addAttribute("lesson", lesson);

        TrainingTemplate jddTemplate = trainingTemplateService.findByIdOrThrow(1L);
        List<TrainingTemplateLesson> templateLessons = jddTemplate.getLessons();

        model.addAttribute("templateLessons", templateLessons);
        return "admin/lesson/lessonCopyFromTemplate";
    }

    @PostMapping("/admin/lekcje/kopiuj")
    public String copyLessonFromTemplate(LessonCopyFromTemplateDto dto) {
        lessonService.copyLessonFromTemplate(dto);
        return "redirect:/admin/szkolenia/" + dto.getTrainingId() + "/lekcje";
    }
}