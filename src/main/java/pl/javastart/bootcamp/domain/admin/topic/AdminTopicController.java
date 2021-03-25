package pl.javastart.bootcamp.domain.admin.topic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/admin/tematy")
@Controller
public class AdminTopicController {

    private TopicService topicService;

    public AdminTopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("")
    public String issueList(Model model) {
        List<Topic> topics = topicService.findAll();
        model.addAttribute("topics", topics);
        return "admin/topic/topicList";
    }

    @GetMapping("/{id}")
    public String previewTopic(@PathVariable Long id, Model model) {
        Topic topic = topicService.findByIdOrThrow(id);
        model.addAttribute("topic", topic);
        return "admin/topic/topic";
    }

    @GetMapping("/dodaj")
    public String addTopicForm(Model model) {
        Topic topic =  topicService.prepareTopicWithSortOrder();
        model.addAttribute("topic", topic);
        model.addAttribute("mode", "add");
        return "admin/topic/topicAddOrEdit";
    }

    @PostMapping("/dodaj")
    public String addTopic(Topic topic) {
        topicService.save(topic);
        return "redirect:/admin/tematy/" + topic.getId();
    }

    @GetMapping("/{id}/edytuj")
    public String editTopicForm(@PathVariable Long id, Model model) {
        Topic topic = topicService.findByIdOrThrow(id);
        model.addAttribute("topic", topic);
        model.addAttribute("mode", "edit");
        return "admin/topic/topicAddOrEdit";
    }

    @PostMapping("/edytuj")
    public String editTopic(Topic topic) {
        topicService.save(topic);
        return "redirect:/admin/tematy/" + topic.getId();
    }


}
