package pl.javastart.bootcamp.domain.admin.task;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.utils.ReorderService;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAllSortedAndNotArchived() {
        return taskRepository.findByArchivedFalseOrderBySortOrder();
    }

    public Task findByIdOrThrow(Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public void save(Task task) {
        taskRepository.save(task);
    }

    public Task prepareTaskWithSortOrder() {
        Task task = new Task();
        List<Task> all = findAllSortedAndNotArchived();
        long sortOrder = 100;
        if (!all.isEmpty()) {
            sortOrder = all.get(all.size() - 1).getSortOrder() + 100;
        }
        task.setSortOrder(sortOrder);
        return task;
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public void archiveTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setArchived(true);
        taskRepository.save(task);
    }

    public void reorder(ReorderDto reorderDto) {
        ReorderService<Task> reorderService = new ReorderService<>(taskRepository);
        reorderService.moveItem(reorderDto.getItemId(), reorderDto.getTargetPosition());
    }
}
