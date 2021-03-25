package pl.javastart.bootcamp.domain.training.lesson.lessontask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

public interface LessonTaskRepository extends JpaRepository<LessonTask, Long> {

    @Modifying
    @Transactional
    void deleteByTaskId(Long id);
}
