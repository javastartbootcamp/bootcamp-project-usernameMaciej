package pl.javastart.bootcamp.domain.admin.template;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingTemplateLessonRepository extends JpaRepository<TrainingTemplateLesson, Long> {

    List<TrainingTemplateLesson> findByTrainingTemplateIdOrderByNumber(Long trainingTemplateId);
}
