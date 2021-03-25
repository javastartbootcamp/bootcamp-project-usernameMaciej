package pl.javastart.bootcamp.domain.training.lesson;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplateLesson;
import pl.javastart.bootcamp.domain.admin.template.TrainingTemplateLessonRepository;
import pl.javastart.bootcamp.domain.admin.topic.Topic;
import pl.javastart.bootcamp.domain.admin.topic.TopicService;
import pl.javastart.bootcamp.domain.admin.training.lesson.LessonAddDto;
import pl.javastart.bootcamp.domain.admin.training.lesson.LessonCopyFromTemplateDto;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupService;
import pl.javastart.bootcamp.domain.training.TrainingRepository;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExercise;
import pl.javastart.bootcamp.domain.training.lesson.lessonexcercise.LessonExerciseRepository;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskRepository;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.training.lesson.LessonWithPointsDto;
import pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution.UserTask;
import pl.javastart.bootcamp.utils.BigDecimalFormatter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonTaskService lessonTaskService;
    private final BigDecimalFormatter bigDecimalFormatter;
    private final TrainingService trainingService;
    private final TopicService topicService;
    private final TrainingRepository trainingRepository;
    private final TrainingTemplateLessonRepository trainingTemplateLessonRepository;
    private final LessonExerciseRepository lessonExerciseRepository;
    private final LessonTaskRepository lessonTaskRepository;
    private final SignupService signupService;

    public LessonService(LessonRepository lessonRepository,
                         LessonTaskService lessonTaskService,
                         BigDecimalFormatter bigDecimalFormatter,
                         TrainingService trainingService,
                         TopicService topicService,
                         TrainingRepository trainingRepository,
                         TrainingTemplateLessonRepository trainingTemplateLessonRepository,
                         LessonExerciseRepository lessonExerciseRepository,
                         LessonTaskRepository lessonTaskRepository,
                         SignupService signupService) {
        this.lessonRepository = lessonRepository;
        this.lessonTaskService = lessonTaskService;
        this.bigDecimalFormatter = bigDecimalFormatter;
        this.trainingService = trainingService;
        this.topicService = topicService;
        this.trainingRepository = trainingRepository;
        this.trainingTemplateLessonRepository = trainingTemplateLessonRepository;
        this.lessonExerciseRepository = lessonExerciseRepository;
        this.lessonTaskRepository = lessonTaskRepository;
        this.signupService = signupService;
    }

    public Lesson findByIdOrThrow(Long id) {
        return lessonRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public List<LessonWithPointsDto> findLessonsForTrainingWithUser(Long trainingId, User user) {
        Signup signup = signupService.findSignupForUserAndTrainingId(user, trainingId);

        List<Lesson> lessons = lessonRepository.findByTrainingIdOrderByNumber(trainingId);
        return lessons.stream()
                .filter(Lesson::isVisible)
                .filter(l -> signup.getLessonTo() == null || l.getNumber() <= signup.getLessonTo() )
                .map(lesson -> toDto(lesson, lessonTaskService.getTaskListMap(user)))
                .collect(Collectors.toList());
    }

    private LessonWithPointsDto toDto(Lesson lesson, Map<LessonTask, List<UserTask>> tasksWithResult) {
        LessonWithPointsDto dto = new LessonWithPointsDto();
        dto.setLesson(lesson);
        dto.setMaxPoints(lesson.getLessonTasks().stream().filter(LessonTask::isMandatory).mapToInt(lt -> lt.getTask().getPoints()).sum());
        dto.setMaxAdditionalPoints(lesson.getLessonTasks().stream().filter(task -> !task.isMandatory()).mapToInt(lt -> lt.getTask().getPoints()).sum());
        dto.setVisible(lesson.isVisible());
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal sumAdditonal = BigDecimal.ZERO;
        for (LessonTask task : lesson.getLessonTasks()) {
            List<UserTask> userTasks = tasksWithResult.get(task);
            if (userTasks != null && !userTasks.isEmpty()) {
                BigDecimal points = userTasks.get(0).getPoints();
                if (points == null) {
                    points = BigDecimal.ZERO;
                }
                if (task.isMandatory()) {
                    sum = sum.add(points);
                } else {
                    sumAdditonal = sumAdditonal.add(points);
                }
            }
        }
        dto.setPoints(sum);
        dto.setPointsFormatted(bigDecimalFormatter.convertDecimalToString(sum));
        dto.setAdditionalPoints(sumAdditonal);
        dto.setAdditionalPointsFormatted(bigDecimalFormatter.convertDecimalToString(sumAdditonal));
        return dto;
    }

    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    public Lesson save(LessonAddDto addLessonDto) {
        Lesson lesson = new Lesson();
        lesson.setTitle(addLessonDto.getTitle());
        lesson.setTraining(trainingService.findByIdOrThrow(addLessonDto.getTrainingId()));
        lesson.setLessonDate(addLessonDto.getLessonDate());
        lesson.setLinkToSlack(addLessonDto.getLinkToSlack());
        lesson.setSortOrder(addLessonDto.getSortOrder());
        lesson.setNumber(addLessonDto.getNumber());
        setupLessonAndVideoLinks(addLessonDto, lesson);
        return lessonRepository.save(lesson);
    }

    private void setupLessonAndVideoLinks(LessonAddDto addLessonDto, Lesson lesson) {
        List<String> lessonLinks = new ArrayList<>();
        List<String> videoLinks = new ArrayList<>();

        if (!StringUtils.isEmpty(lesson.getLessonLinks())) {
            lessonLinks.add(lesson.getLessonLinks());
        }

        if (!StringUtils.isEmpty(lesson.getVideoLinks())) {
            videoLinks.add(lesson.getVideoLinks());
        }

        if (addLessonDto.getTopicIds() != null) {
            for (long topicId : addLessonDto.getTopicIds()) {
                Topic topic = topicService.findByIdOrThrow(topicId);
                if (!Strings.isNullOrEmpty(topic.getLessonLinks())) {
                    lessonLinks.add(topic.getLessonLinks());
                }
                if (!Strings.isNullOrEmpty(topic.getVideoLinks())) {
                    videoLinks.add(topic.getVideoLinks());
                }
            }
        }

        lesson.setLessonLinks(String.join("\n", lessonLinks));
        lesson.setVideoLinks(String.join("\n", videoLinks));
    }

    public Lesson addTopicsToLesson(LessonAddDto addLessonDto) {
        Lesson lesson = findByIdOrThrow(addLessonDto.getLessonId());
        setupLessonAndVideoLinks(addLessonDto, lesson);
        return lessonRepository.save(lesson);
    }

    public LessonAddDto prepareLessonAddDto(Long trainingId) {
        LessonAddDto dto = new LessonAddDto();
        List<Lesson> all = lessonRepository.findByTrainingIdOrderByNumber(trainingId);
        long sortOrder = 100;
        long number = 1;
        if (!all.isEmpty()) {
            // sortOrder = all.get(all.size() - 1).getSortOrder() + 100;
            number = all.get(all.size() - 1).getNumber() + 1;
        }
        dto.setNumber(number);
        dto.setSortOrder(sortOrder);
        dto.setTrainingId(trainingId);
        return dto;
    }

    public LessonCopyFromTemplateDto prepareLessonCopyFromTemplateDto(Long trainingId) {
        LessonCopyFromTemplateDto dto = new LessonCopyFromTemplateDto();
        List<Lesson> all = lessonRepository.findByTrainingIdOrderByNumber(trainingId);
        long sortOrder = 100;
        long number = 1;
        if (!all.isEmpty()) {
            // sortOrder = all.get(all.size() - 1).getSortOrder() + 100;
            number = all.get(all.size() - 1).getNumber() + 1;
        }
        dto.setNumber(number);
        dto.setSortOrder(sortOrder);
        dto.setTrainingId(trainingId);
        return dto;
    }

    public void copyLessonFromTemplate(LessonCopyFromTemplateDto dto) {

        TrainingTemplateLesson trainingTemplateLesson = trainingTemplateLessonRepository.findById(dto.getTemplateLessonId()).orElseThrow();

        Lesson lessonToCopy = trainingTemplateLesson.getLesson();

        Lesson lesson = new Lesson();

        lesson.setTraining(trainingRepository.findById(dto.getTrainingId()).orElseThrow());
        lesson.setNumber(dto.getNumber());
        lesson.setLessonDate(dto.getLessonDate());
        lesson.setLessonLinks(lessonToCopy.getLessonLinks());
        lesson.setVideoLinks(lessonToCopy.getVideoLinks());
        lesson.setTitle(lessonToCopy.getTitle());
        lessonRepository.save(lesson);

        copyExercises(lessonToCopy, lesson);
        copyTasks(trainingTemplateLesson, lesson);
    }

    private void copyExercises(Lesson lessonToCopy, Lesson targetLesson) {
        Set<LessonExercise> exercises = lessonToCopy.getLessonExercises()
                .stream()
                .map(exerciseToCopy -> {
                    LessonExercise lessonExercise = new LessonExercise();
                    lessonExercise.setLesson(targetLesson);
                    lessonExercise.setNumber(exerciseToCopy.getNumber());
                    lessonExercise.setTask(exerciseToCopy.getTask());
                    return lessonExercise;
                })
                .collect(Collectors.toSet());

        lessonExerciseRepository.saveAll(exercises);
    }

    private void copyTasks(TrainingTemplateLesson trainingTemplateLesson, Lesson targetLesson) {

        Lesson lessonToCopy = trainingTemplateLesson.getLesson();

        Set<LessonTask> tasks = lessonToCopy.getLessonTasks()
                .stream()
                .map(taskToCopy -> {
                    LessonTask lessonTask = new LessonTask();
                    lessonTask.setLesson(targetLesson);
                    lessonTask.setNumber(taskToCopy.getNumber());
                    lessonTask.setTask(taskToCopy.getTask());
                    lessonTask.setDeadline(calculateDeadline(targetLesson, taskToCopy));
                    lessonTask.setMandatory(taskToCopy.isMandatory());
                    return lessonTask;
                })
                .collect(Collectors.toSet());

        lessonTaskRepository.saveAll(tasks);
    }

    private LocalDateTime calculateDeadline(Lesson targetLesson, LessonTask taskToCopy) {
        LocalDate deadlineDate = targetLesson.getLessonDate().plusDays(taskToCopy.getDeadlineDays());
        return LocalDateTime.of(deadlineDate, taskToCopy.getDeadlineHour());
    }
}
