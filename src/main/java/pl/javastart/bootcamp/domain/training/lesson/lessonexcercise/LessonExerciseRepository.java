package pl.javastart.bootcamp.domain.training.lesson.lessonexcercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

public interface LessonExerciseRepository extends JpaRepository<LessonExercise, Long> {

    @Modifying
    @Transactional
    void deleteByTaskId(Long id);
}
