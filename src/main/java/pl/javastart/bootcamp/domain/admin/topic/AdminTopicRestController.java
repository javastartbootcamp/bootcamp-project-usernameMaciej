package pl.javastart.bootcamp.domain.admin.topic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.javastart.bootcamp.domain.admin.task.ReorderDto;

@Controller
@RequestMapping("/api/tematy")
public class AdminTopicRestController {

    private final TopicService topicService;

    public AdminTopicRestController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping("/pozycja")
    @ResponseBody
    public String movePosition(@RequestBody ReorderDto reorderDto) {
        topicService.reorder(reorderDto);
        return "ok";
    }
}
