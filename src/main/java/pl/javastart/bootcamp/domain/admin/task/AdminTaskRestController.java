package pl.javastart.bootcamp.domain.admin.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/zadania")
public class AdminTaskRestController {
    
    private final TaskService taskService;

    public AdminTaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/pozycja")
    @ResponseBody
    public String movePosition(@RequestBody ReorderDto reorderDto) {
        taskService.reorder(reorderDto);
        return "ok";
    }
}
