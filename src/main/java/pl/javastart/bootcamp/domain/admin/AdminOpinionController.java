package pl.javastart.bootcamp.domain.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.opinion.Opinion;
import pl.javastart.bootcamp.domain.opinion.OpinionService;

import java.util.Optional;

@RequestMapping("/admin/opinie")
@Controller
public class AdminOpinionController {

    private OpinionService opinionService;

    public AdminOpinionController(OpinionService opinionService) {
        this.opinionService = opinionService;
    }

    @GetMapping("")
    public String pages(Model model) {
        model.addAttribute("opinions", opinionService.findAll());
        return "admin/opinion/opinions";
    }

    @GetMapping("/dodaj")
    public String add(Model model) {
        model.addAttribute("opinion", new Opinion());
        return "admin/opinion/editOpinion";
    }

    @PostMapping("/dodaj")
    public String addOpinion(Opinion opinion) {
        opinionService.insert(opinion);
        return "redirect:/admin/opinie";
    }

    @GetMapping("/{id}/edytuj")
    public String editPage(@PathVariable Long id, Model model) {
        Optional<Opinion> pageOptional = opinionService.findById(id);

        if (pageOptional.isPresent()) {
            model.addAttribute("opinion", pageOptional.get());
            return "admin/opinion/editOpinion";
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PostMapping("/edytuj")
    public String updateOpinion(Opinion opinion) {
        opinionService.update(opinion);
        return "redirect:/admin/opinie";
    }
}
