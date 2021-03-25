package pl.javastart.bootcamp.domain.training.lesson.lessonexcercise;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.admin.task.Task;
import pl.javastart.bootcamp.domain.admin.task.TaskService;
import pl.javastart.bootcamp.domain.admin.template.AddExerciseToTemplateLessonDto;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplateLesson;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplateLessonRepository;
import pl.javastart.bootcamp.domain.admin.training.exercise.AddLessonExerciseDto;
import pl.javastart.bootcamp.domain.training.lesson.Lesson;
import pl.javastart.bootcamp.domain.training.lesson.LessonRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Service
public class LessonExerciseService {

    private final LessonExerciseRepository lessonExerciseRepository;
    private final TaskService taskService;
    private final EntityManager entityManager;
    private final TrainingTemplateLessonRepository trainingTemplateLessonRepository;
    private final LessonRepository lessonRepository;

    public LessonExerciseService(LessonExerciseRepository lessonExerciseRepository,
                                 TaskService taskService,
                                 EntityManager entityManager,
                                 TrainingTemplateLessonRepository trainingTemplateLessonRepository,
                                 LessonRepository lessonRepository) {
        this.lessonExerciseRepository = lessonExerciseRepository;
        this.taskService = taskService;
        this.entityManager = entityManager;
        this.trainingTemplateLessonRepository = trainingTemplateLessonRepository;
        this.lessonRepository = lessonRepository;
    }

    public LessonExercise findByIdOrThrow(Long id) {
        return lessonExerciseRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public void delete(LessonExercise task) {
        lessonExerciseRepository.delete(task);
    }

    public void save(LessonExercise lessonExercise) {
        lessonExerciseRepository.save(lessonExercise);
    }

    public void save(AddLessonExerciseDto addLessonExerciseDto) {

        long counter = 1;

        Lesson lesson = lessonRepository.findById(addLessonExerciseDto.getLessonId()).orElseThrow();
        List<LessonExercise> lessonExercises = lesson.getLessonExercises();

        if (lessonExercises.size() > 0) {
            counter = lessonExercises.get(lessonExercises.size() - 1).getNumber() + 1;
        }

        for (long tasksId : addLessonExerciseDto.getTaskIds()) {
            LessonExercise lessonExercise = new LessonExercise();
            lessonExercise.setLesson(entityManager.getReference(Lesson.class, addLessonExerciseDto.getLessonId()));
            lessonExercise.setNumber(counter++);

            Task task = taskService.findByIdOrThrow(tasksId);
            lessonExercise.setTask(task);
            lessonExerciseRepository.save(lessonExercise);

            task.setLastUsed(LocalDate.now());
            taskService.save(task);
        }
    }

    public void save(AddExerciseToTemplateLessonDto addLessonExerciseDto) {

        long counter = 1;

        Long templateLessonId = addLessonExerciseDto.getTemplateLessonId();

        TrainingTemplateLesson trainingTemplateLesson = trainingTemplateLessonRepository.findById(templateLessonId).orElseThrow();
        List<LessonExercise> lessonExercises = trainingTemplateLesson.getLesson().getLessonExercises();

        if (lessonExercises.size() > 0) {
            counter = lessonExercises.get(lessonExercises.size() - 1).getNumber() + 1;
        }

        for (long tasksId : addLessonExerciseDto.getTaskIds()) {
            LessonExercise lessonExercise = new LessonExercise();
            lessonExercise.setLesson(trainingTemplateLesson.getLesson());
            lessonExercise.setNumber(counter++);
            Task task = taskService.findByIdOrThrow(tasksId);
            lessonExercise.setTask(task);
            lessonExerciseRepository.save(lessonExercise);

            task.setLastUsed(LocalDate.now());
            taskService.save(task);
        }
    }


}
