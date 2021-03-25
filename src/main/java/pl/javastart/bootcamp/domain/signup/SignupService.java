package pl.javastart.bootcamp.domain.signup;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.user.User;
import pl.javastart.bootcamp.domain.user.role.Role;
import pl.javastart.bootcamp.domain.user.role.UserRole;

import java.nio.file.ReadOnlyFileSystemException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SignupService {

    private SignupRepository signupRepository;

    public SignupService(SignupRepository signupRepository) {
        this.signupRepository = signupRepository;
    }

    public Optional<Signup> findById(Long id) {
        return signupRepository.findById(id);
    }

    public void removeAllSignupsForUser(User user) {
        List<Signup> signups = user.getSignups();
        signupRepository.deleteAll(signups);
    }

    public Signup createSignup(User user, Training training, String message, String financingMethod) {
        Signup signup = new Signup();
        signup.setUser(user);
        signup.setSignupDate(LocalDateTime.now());
        signup.setTraining(training);
        signup.setNote(message);
        signup.setFinancingMethod(financingMethod);
        signup.setStatus(SignupStatus.NEW);
        signup.setDeposit(training.getDeposit());
        signup.setPrice(training.getPrice());
        signup.setHomeworkExtensionsLeft(4);
        return signupRepository.save(signup);
    }

    public void update(Signup signup) {
        signupRepository.save(signup);
    }

    public List<Signup> findByUser(User user) {
        return signupRepository.findByUser(user);
    }

    List<Signup> findByUserAndWithAccess(User user) {
        return signupRepository.findByUserAndCanSeeContentIsTrue(user);
    }

    public Signup findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(ReadOnlyFileSystemException::new);
    }

    public List<Signup> findByStatus(SignupStatus status) {
        return signupRepository.findByStatus(status);
    }

    public Signup findSignupForUserAndLessonTask(User user, LessonTask lessonTask) {
        Long trainingId = lessonTask.getLesson().getTraining().getId();
        return findSignupForUserAndTrainingId(user, trainingId);
    }

    public Signup findSignupForUserAndTrainingId(User user, Long trainingId) {
        List<Signup> signups = findByUser(user);
        if (signups.isEmpty()) {
            if (isAdmin(user)) {
                Signup fakeSignup = new Signup();
                fakeSignup.setUser(user);
                return fakeSignup;
            }
            throw new IllegalStateException("No signups found for user: " + user.getEmail());
        } else if (signups.size() == 1) {
            return signups.get(0);
        } else {
            return signups
                    .stream()
                    .filter(s -> s.getTraining().getId().equals(trainingId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No signups found for user: " + user.getEmail() + " and trainingId: " + trainingId));
        }
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream().map(UserRole::getRole).anyMatch(r -> r.equals(Role.ROLE_ADMIN));
    }

    public void lowerHomeworkExtensionsForSignup(Signup signup) {
        signup.setHomeworkExtensionsLeft(signup.getHomeworkExtensionsLeft() - 1);
        signupRepository.save(signup);
    }
}
