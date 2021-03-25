package pl.javastart.bootcamp.domain.admin.task.rating;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.javastart.bootcamp.domain.user.User;

import java.util.Optional;

public interface TaskRatingRepository extends JpaRepository<TaskRating, Long> {

    Optional<TaskRating> findByUserAndTaskId(User user, Long taskId);

}
