package pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.javastart.bootcamp.config.JavaStartProperties;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.signup.SignupService;
import pl.javastart.bootcamp.domain.slack.SlackService;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTaskService;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.UserService;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserTaskSolutionService {

    private final UserTaskEntryRepository userTaskEntryRepository;
    private final LessonTaskService lessonTaskService;
    private final UserService userService;
    private final UserTaskRepository userTaskRepository;
    private final SlackService slackService;
    private final SignupService signupService;
    private final JavaStartProperties javaStartProperties;

    public UserTaskSolutionService(UserTaskEntryRepository userTaskEntryRepository,
                                   LessonTaskService lessonTaskService,
                                   UserService userService, UserTaskRepository userTaskRepository,
                                   SlackService slackService,
                                   SignupService signupService,
                                   JavaStartProperties javaStartProperties) {
        this.userTaskEntryRepository = userTaskEntryRepository;
        this.lessonTaskService = lessonTaskService;
        this.userService = userService;
        this.userTaskRepository = userTaskRepository;
        this.slackService = slackService;
        this.signupService = signupService;
        this.javaStartProperties = javaStartProperties;
    }

    public static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .replaceAll("\\.\\d+", "")
                .toLowerCase();
    }

    public void handleSolutionSentByUser(UserTaskSolutionDto userTaskSolutionDto) {

        User user = userService.getCurrentUser();
        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(userTaskSolutionDto.getLessonTaskId());

        Optional<UserTask> userTaskOptional = lessonTaskService.findUserTaskByLessonTaskIdAndUserId(userTaskSolutionDto.getLessonTaskId(), user.getId());

        UserTask userTask;

        if (userTaskOptional.isPresent()) {
            userTask = userTaskOptional.get();
            userTask.setSolutionUrl(userTaskSolutionDto.getUrl());
        } else {
            userTask = new UserTask();
            userTask.setUser(user);
            userTask.setLessonTask(lessonTask);
            userTask.setSolutionUrl(userTaskSolutionDto.getUrl());
            userTask.setDeadline(lessonTask.getDeadline());
        }
        userTask.setToBeChecked(true);

        userTaskRepository.save(userTask);

        String solutionAsLink = "<a target=\"_blank\" href=\"" + userTaskSolutionDto.getUrl() + "\">" + userTaskSolutionDto.getUrl() + "</a>";

        UserTaskEntry userTaskEntry = UserTaskEntry.builder()
                .text("Podesłanie rozwiązanie do sprawdzenia: " + solutionAsLink)
                .userTask(userTask)
                .dateTime(ZonedDateTime.now())
                .build();

        String taskNumber = lessonTask.getLesson().getNumber() + "." + lessonTask.getNumber();
        String name = taskNumber + ". " + lessonTask.getTask().getName();

        String taskNameAsLink = prepareTaskNameAsLink(lessonTask);
        String text = "Zadanie " + taskNameAsLink + " zostało wysłane do sprawdzenia. Link do kodu: " + userTask.getSolutionUrl();

        String adminUrl = javaStartProperties.getFullDomainAddress() + "/admin/lekcje/zadania/" + lessonTask.getId() + "/ocena?userId=" + userTask.getUser().getId();
        text += ". <" + adminUrl + "|[✅]>";

        sendSlackNotification(user, lessonTask, text);

        userTaskEntryRepository.save(userTaskEntry);
    }

    private void sendSlackNotification(User user, LessonTask lessonTask, String text) {
        try {
            String channelId = findChannelId(user, lessonTask);
            String botToken = lessonTask.getLesson().getTraining().getSlackBotAccessToken();
            slackService.sendSlackNotification(text, channelId, botToken);
        } catch (Exception e) {
            System.out.println("Could not send slack notification");
            e.printStackTrace();
        }
    }

    private String findChannelId(User user, LessonTask lessonTask) {
        return signupService.findSignupForUserAndLessonTask(user, lessonTask).getSlackChannelId();
    }

    public List<UserTaskEntry> findUserTaskEntriesSortedByDate(UserTask userTask) {
        return userTaskEntryRepository.findByUserTaskOrderByDateTimeDesc(userTask);
    }

    public List<UserTaskWithLastEntryDto> findNotCheckedForTrainingId(Long trainingId) {
        List<UserTask> allToBeChecked = userTaskRepository.findByToBeChecked(true);

        return allToBeChecked.stream()
                .filter(userTask -> userTask.getLessonTask().getLesson().getTraining().getId().equals(trainingId))
                .map(this::getUserTaskWithLastEntryDto)
                .sorted(Comparator.comparing(UserTaskWithLastEntryDto::getLastEntryDate))
                .collect(Collectors.toList());
    }

    private UserTaskWithLastEntryDto getUserTaskWithLastEntryDto(UserTask userTask) {
        List<UserTaskEntry> entries = userTask.getEntries();
        UserTaskEntry lastEntry = entries.get(entries.size() - 1);

        UserTaskWithLastEntryDto dto = new UserTaskWithLastEntryDto();
        dto.setLastEntryDate(lastEntry.getDateTime());

        User user = userTask.getUser();
        dto.setUsername(user.getFirstName() + " " + user.getLastName());

        LessonTask lessonTask = userTask.getLessonTask();
        dto.setLessonTaskId(lessonTask.getId());
        dto.setUserId(user.getId());

        dto.setTaskNumberAndName(lessonTask.getLesson().getNumber() + "." + lessonTask.getNumber() + " " + lessonTask.getTask().getName());

        return dto;
    }


    public void handleRatingSendByTrainer(UserTask data) {

        UserTask userTask = userTaskRepository.findById(data.getId()).orElseThrow();
        userTask.setPoints(data.getPoints());
        userTask.setSolutionUrl(data.getSolutionUrl());
        userTask.setToBeChecked(false);
        userTaskRepository.save(userTask);

        String text = "Zadanie ocenione na " + userTask.getPoints() + " pkt.";
        UserTaskEntry userTaskEntry = UserTaskEntry.builder()
                .text(text)
                .userTask(userTask)
                .dateTime(ZonedDateTime.now())
                .build();


        if (userTask.getPoints() != null) {
            LessonTask lessonTask = userTask.getLessonTask();
            String taskNameAsLink = prepareTaskNameAsLink(lessonTask);

            DecimalFormat df = new DecimalFormat("0.##");
            String achievedPoints = df.format(userTask.getPoints());
            String possiblePoints = df.format(lessonTask.getTask().getPoints());

            String solutionUrl = " ";
            if (!StringUtils.isEmpty(userTask.getSolutionUrl())) {
                solutionUrl = " (<" + userTask.getSolutionUrl() + "|kod>) ";
            }

            String slackText = "Zadanie " + taskNameAsLink + solutionUrl + "zostało ocenione na " + achievedPoints + "/" + possiblePoints + " pkt.";

            if (achievedPoints.equals(possiblePoints)) {
                String[] winEmojis = new String[]{"🎉", "💪", "✌", "🏆", "👌", "🕺💃", "🙌"};
                String[] winWords = new String[]{"Elegancko", "Super", "Brawo", "No i o to chodzi", "Profeska", "Pico Bello", "Sztosik", ""};
                String winEmoji = winEmojis[new Random().nextInt(winEmojis.length)];
                String winWord = winWords[new Random().nextInt(winWords.length)];
                slackText += " " + winWord + "! " + winEmoji;
            } else {
                if (userTask.getDeadline().isAfter(LocalDateTime.now())) {
                    Duration timeLeft = Duration.between(LocalDateTime.now(), userTask.getDeadline());
                    String timeLeftString = "Masz jeszcze " + humanReadableFormat(timeLeft) + " na ewenualne poprawki.";
                    slackText += " " + timeLeftString;
                }
            }

            sendSlackNotification(userTask.getUser(), lessonTask, slackText);
        }


        userTaskEntryRepository.save(userTaskEntry);
    }

    private String prepareTaskNameAsLink(LessonTask lessonTask) {
        String url = javaStartProperties.getFullDomainAddress() + "/konto/zadanie/" + lessonTask.getId();
        String taskNumber = lessonTask.getLesson().getNumber() + "." + lessonTask.getNumber();
        String name = taskNumber + ". " + lessonTask.getTask().getName();
        return "<" + url + "|" + name + ">";
    }

    public void userStartedSolving(UserTask userTask) {
        String repoAsLink = "<a target=\"_blank\" href=\"" + userTask.getSolutionUrl() + "\">" + userTask.getSolutionUrl() + "</a>";
        String text = "Rozpoczęcie rozwiązywania zadania. Repozytorium: " + repoAsLink;

        UserTaskEntry userTaskEntry = UserTaskEntry.builder()
                .text(text)
                .userTask(userTask)
                .dateTime(ZonedDateTime.now())
                .build();

        userTaskEntryRepository.save(userTaskEntry);
    }

    public void extendDeadlineForLessonTaskAndUser(Long lessonTaskId, User user) {
        LessonTask lessonTask = lessonTaskService.findByIdOrThrow(lessonTaskId);

        Signup signup = signupService.findSignupForUserAndLessonTask(user, lessonTask);

        if (signup.getHomeworkExtensionsLeft() <= 0) {
            return;
        }

        Optional<UserTask> userTaskOptional = lessonTaskService.findUserTaskByLessonTaskIdAndUserId(lessonTaskId, user.getId());

        UserTask userTask;
        LocalDateTime currentDeadline = lessonTask.getDeadline();

        if (userTaskOptional.isPresent()) {
            userTask = userTaskOptional.get();
            currentDeadline = userTask.getDeadline();
        } else {
            userTask = new UserTask();
            userTask.setUser(user);
            userTask.setLessonTask(lessonTask);
        }
        userTask.setDeadline(currentDeadline.plusWeeks(1));

        userTaskRepository.save(userTask);

        signupService.lowerHomeworkExtensionsForSignup(signup);

        String text = "Kursant przedłużył czas na oddanie zadania o tydzień."
                + " Nowy termin to: " + userTask.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + ". Pozostało przedłużeń: " + signup.getHomeworkExtensionsLeft();

        UserTaskEntry userTaskEntry = UserTaskEntry.builder()
                .text(text)
                .userTask(userTask)
                .dateTime(ZonedDateTime.now())
                .build();
        userTaskEntryRepository.save(userTaskEntry);

        String slackText = prepareTaskNameAsLink(lessonTask) + " -> " + text;

        sendSlackNotification(user, lessonTask, slackText);


    }
}
