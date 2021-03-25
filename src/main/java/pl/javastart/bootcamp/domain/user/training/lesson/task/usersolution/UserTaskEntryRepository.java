package pl.javastart.bootcamp.domain.user.training.lesson.task.usersolution;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTaskEntryRepository extends JpaRepository<UserTaskEntry, Long> {
    List<UserTaskEntry> findByUserTaskOrderByDateTimeDesc(UserTask userTask);
}
