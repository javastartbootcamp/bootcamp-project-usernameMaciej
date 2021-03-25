package pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.javastart.bootcamp.domain.training.lesson.lessontask.LessonTask;
import pl.javastart.bootcamp.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    List<UserTask> findByUser(User user);

    List<UserTask> findByLessonTask(LessonTask task);

    Optional<UserTask> findByLessonTaskIdAndUserId(Long lessonTaskId, Long userId);

    List<UserTask> findByToBeChecked(boolean toBeChecked);
}
