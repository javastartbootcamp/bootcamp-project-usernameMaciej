package pl.javastart.bootcamp.domain.admin.template;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.javastart.bootcamp.domain.admin.task.TaskService;
import pl.javastart.bootcamp.domain.admin.topic.TopicService;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExercise;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExerciseService;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;

import java.time.LocalTime;
import java.util.List;

@RequestMapping("/admin/szablony")
@Controller
public class AdminTrainingTemplateController {

    private final TrainingTemplateService trainingTemplateService;
    private final TopicService topicService;
    private final LessonExerciseService lessonExerciseService;
    private final TaskService taskService;
    private final LessonTaskService lessonTaskService;

    public AdminTrainingTemplateController(TrainingTemplateService trainingTemplateService,
                                           TopicService topicService,
                                           LessonExerciseService lessonExerciseService,
                                           TaskService taskService,
                                           LessonTaskService lessonTaskService) {
        this.trainingTemplateService = trainingTemplateService;
        this.topicService = topicService;
        this.lessonExerciseService = lessonExerciseService;
        this.taskService = taskService;
        this.lessonTaskService = lessonTaskService;
    }

    @GetMapping("")
    public String templateList(Model model) {
        List<TrainingTemplate> templates = trainingTemplateService.findAll();
        model.addAttribute("templates", templates);
        return "admin/template/templateList";
    }

    @GetMapping("/dodaj")
    public String showAddForm(Model model) {
        TrainingTemplate trainingTemplate = new TrainingTemplate();
        model.addAttribute("template", trainingTemplate);
        model.addAttribute("mode", "add");
        return "admin/template/templateAddOrEdit";
    }

    @PostMapping("/dodaj")
    public String add(TrainingTemplate trainingTemplate) {
        trainingTemplateService.insert(trainingTemplate);
        return "redirect:/admin/szablony";
    }

    @GetMapping("/{id}")
    public String showTemplate(Model model, @PathVariable Long id) {
        TrainingTemplate template = trainingTemplateService.findByIdOrThrow(id);
        model.addAttribute("template", template);
        return "admin/template/template";
    }

    @GetMapping("/{templateId}/lekcje/dodaj")
    public String showAddTemplateLessonForm(@PathVariable Long templateId, Model model) {

        TrainingTemplateLessonDto trainingTemplateLesson = trainingTemplateService.createTemplateLessonForTemplateId(templateId);

        model.addAttribute("trainingTemplateLesson", trainingTemplateLesson);
        model.addAttribute("mode", "add");

        return "admin/template/lesson/addOrEditTemplateLesson";
    }

    @GetMapping("/{templateId}/lekcje/{templateLessonId}")
    public String showTemplateLesson(@PathVariable Long templateLessonId, Model model) {
        TrainingTemplateLesson trainingTemplateLesson = trainingTemplateService.findTemplateLessonByIdOrThrow(templateLessonId);
        model.addAttribute("templateLesson", trainingTemplateLesson);
        model.addAttribute("topics", topicService.findAll());
        return "admin/template/lesson/templateLesson";
    }

    @PostMapping("/{templateId}/lekcje/dodaj")
    public String addLessonToTemplate(TrainingTemplateLessonDto dto) {

        TrainingTemplateLesson insertedLesson = trainingTemplateService.insertLesson(dto);

        return "redirect:/admin/szablony/" + insertedLesson.getTrainingTemplate().getId() + "/lekcje/" + insertedLesson.getId() + "/edycja";
    }

    @GetMapping("/{templateId}/lekcje/{templateLessonId}/edycja")
    public String showEditTemplateLessonForm(@PathVariable Long templateLessonId, Model model) {
        TrainingTemplateLesson templateLesson = trainingTemplateService.findTemplateLessonByIdOrThrow(templateLessonId);

        TrainingTemplateLessonDto dto = new TrainingTemplateLessonDto();
        dto.setTrainingTemplateId(templateLesson.getTrainingTemplate().getId());
        dto.setVideoLinks(templateLesson.getLesson().getVideoLinks());
        dto.setLessonLinks(templateLesson.getLesson().getLessonLinks());
        dto.setNumber(templateLesson.getNumber());
        dto.setId(templateLesson.getId());
        dto.setTitle(templateLesson.getLesson().getTitle());
        dto.setLessonId(templateLesson.getLesson().getId());

        model.addAttribute("trainingTemplateLesson", dto);
        model.addAttribute("mode", "edit");
        model.addAttribute("topics", topicService.findAll());

        return "admin/template/lesson/addOrEditTemplateLesson";
    }

    @PostMapping("/{templateId}/lekcje/{templateLessonId}/edycja")
    public String editTemplateLessonForm(TrainingTemplateLessonDto dto, Model model) {
        trainingTemplateService.updateTemplateLesson(dto);
        model.addAttribute("trainingTemplateLesson", dto);
        model.addAttribute("mode", "edit");
        return "redirect:/admin/szablony/" + dto.getTrainingTemplateId() + "/lekcje/" + dto.getId();
    }

    @GetMapping("/{templateId}/lekcje/{templateLessonId}/cwiczenia/{exerciseId}/usun")
    public String deleteExercise(@PathVariable Long templateId, @PathVariable Long templateLessonId, @PathVariable Long exerciseId) {
        LessonExercise exercise = lessonExerciseService.findByIdOrThrow(exerciseId);
        lessonExerciseService.delete(exercise);
        return "redirect:/admin/szablony/" + templateId + "/lekcje/" + templateLessonId;
    }

    @GetMapping("/{templateId}/lekcje/{templateLessonId}/zadania/{taskId}/usun")
    public String deleteTask(@PathVariable Long templateId, @PathVariable Long templateLessonId, @PathVariable Long taskId) {
        LessonTask task = lessonTaskService.findByIdOrThrow(taskId);
        lessonTaskService.delete(task);
        return "redirect:/admin/szablony/" + templateId + "/lekcje/" + templateLessonId;
    }

    @GetMapping("/{templateId}/lekcje/{templateLessonId}/zadania/dodaj")
    public String addTasks(Model model, @PathVariable Long templateId, @PathVariable Long templateLessonId) {

        AddTaskToTemplateLessonDto dto = new AddTaskToTemplateLessonDto();
        dto.setTemplateId(templateId);
        dto.setTemplateLessonId(templateLessonId);
        dto.setDeadlineDays(1);
        dto.setDeadlineHour(LocalTime.of(23, 59));
        model.addAttribute("dto", dto);
        model.addAttribute("tasks", taskService.findAllSortedAndNotArchived());

        return "admin/template/lesson/templateLessonTaskAdd";
    }

    @GetMapping("/{templateId}/lekcje/{templateLessonId}/cwiczenia/dodaj")
    public String showAddExerciseView(@PathVariable Long templateId, @PathVariable Long templateLessonId, Model model) {

        AddExerciseToTemplateLessonDto dto = new AddExerciseToTemplateLessonDto();
        dto.setTemplateLessonId(templateLessonId);
        dto.setTemplateId(templateId);

        model.addAttribute("dto", dto);
        model.addAttribute("tasks", taskService.findAllSortedAndNotArchived());

        return "admin/template/lesson/templateLessonExerciseAdd";
    }

    @PostMapping("/lekcje/cwiczenia/dodaj")
    public String addExerciseToLesson(AddExerciseToTemplateLessonDto dto) {
        lessonExerciseService.save(dto);
        Long templateId = dto.getTemplateId();
        Long templateLessonId = dto.getTemplateLessonId();
        return String.format("redirect:/admin/szablony/%d/lekcje/%d", templateId, templateLessonId);
    }

    @PostMapping("/lekcje/zadania/dodaj")
    public String addTaskToLesson(AddTaskToTemplateLessonDto dto) {
        lessonTaskService.save(dto);
        Long templateId = dto.getTemplateId();
        Long templateLessonId = dto.getTemplateLessonId();
        return String.format("redirect:/admin/szablony/%d/lekcje/%d", templateId, templateLessonId);
    }


    @GetMapping("/{templateId}/lekcje/{templateLessonId}/zadania/{taskId}/obowiazkowe")
    public String toggleMandatory(@PathVariable Long templateId, @PathVariable Long templateLessonId,
                                  @PathVariable Long taskId, @RequestParam(name = "zmien") Boolean mandatory) {
        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(taskId);
        lessonTask.setMandatory(mandatory);
        lessonTaskService.save(lessonTask);
        return String.format("redirect:/admin/szablony/%d/lekcje/%d", templateId, templateLessonId);
    }

    @GetMapping("/{templateId}/lekcje/{templateLessonId}/zadania/{lessonTaskId}/edytuj")
    public String showEditTaskView(@PathVariable Long templateId, @PathVariable Long templateLessonId,
                                   @PathVariable Long lessonTaskId, Model model) {

        EditTaskInTemplateLessonDto dto = new EditTaskInTemplateLessonDto();
        dto.setTemplateId(templateId);
        dto.setTemplateLessonId(templateLessonId);
        dto.setLessonTaskId(lessonTaskId);

        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(lessonTaskId);
        dto.setMandatory(lessonTask.isMandatory());
        dto.setNumber(lessonTask.getNumber());
        dto.setDeadlineDays(lessonTask.getDeadlineDays());
        dto.setDeadlineHour(lessonTask.getDeadlineHour());
        model.addAttribute("taskId", lessonTask.getTask().getId());
        model.addAttribute("templateId", templateId);
        model.addAttribute("templateLessonId", templateLessonId);
        model.addAttribute("dto", dto);

        return "admin/template/lesson/templateLessonTaskEdit";
    }

    @PostMapping("/lekcje/zadania/edytuj")
    public String editTask(EditTaskInTemplateLessonDto dto) {

        Long lessonTaskId = dto.getLessonTaskId();
        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(lessonTaskId);
        lessonTask.setNumber(dto.getNumber());
        lessonTask.setMandatory(dto.isMandatory());
        lessonTask.setDeadlineDays(dto.getDeadlineDays());
        lessonTask.setDeadlineHour(dto.getDeadlineHour());
        lessonTaskService.save(lessonTask);

        return String.format("redirect:/admin/szablony/%d/lekcje/%d", dto.getTemplateId(), dto.getTemplateLessonId());
    }

    @GetMapping("/{templateId}/lekcje/{templateLessonId}/cwiczenia/{lessonExerciseId}/edytuj")
    public String showEditExerciseView(@PathVariable Long templateId, @PathVariable Long templateLessonId,
                                       @PathVariable Long lessonExerciseId, Model model) {

        EditExerciseInTemplateLessonDto dto = new EditExerciseInTemplateLessonDto();
        dto.setTemplateId(templateId);
        dto.setTemplateLessonId(templateLessonId);
        dto.setLessonExerciseId(lessonExerciseId);

        LessonExercise lessonExercise = lessonExerciseService.findByIdOrThrow(lessonExerciseId);
        dto.setNumber(lessonExercise.getNumber());
        model.addAttribute("taskId", lessonExercise.getTask().getId());
        model.addAttribute("templateId", templateId);
        model.addAttribute("templateLessonId", templateLessonId);
        model.addAttribute("dto", dto);

        return "admin/template/lesson/templateLessonExerciseEdit";
    }

    @PostMapping("/lekcje/cwiczenia/edytuj")
    public String editExercise(EditExerciseInTemplateLessonDto dto) {

        Long lessonExerciseId = dto.getLessonExerciseId();
        LessonExercise lessonTask = lessonExerciseService.findByIdOrThrow(lessonExerciseId);
        lessonTask.setNumber(dto.getNumber());
        lessonExerciseService.save(lessonTask);

        return String.format("redirect:/admin/szablony/%d/lekcje/%d", dto.getTemplateId(), dto.getTemplateLessonId());
    }

}
