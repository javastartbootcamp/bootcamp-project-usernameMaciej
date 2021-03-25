package pl.javastart.bootcamp.domain.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.page.Page;
import pl.javastart.bootcamp.domain.page.PageService;

import java.util.Optional;

@RequestMapping("/admin/strony")
@Controller
public class AdminPageController {

    private PageService pageService;

    public AdminPageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("")
    public String pages(Model model) {
        model.addAttribute("pages", pageService.findAll());
        return "admin/page/pages";
    }

    @GetMapping("/dodaj")
    public String add(Model model) {
        model.addAttribute("page", new Page());
        return "admin/page/editPage";
    }

    @PostMapping("/dodaj")
    public String addPage(Page page) {
        pageService.insert(page);
        return "redirect:/admin/strony";
    }

    @GetMapping("/{id}/edytuj")
    public String editPage(@PathVariable Long id, Model model) {
        Optional<Page> pageOptional = pageService.findById(id);

        if (pageOptional.isPresent()) {
            model.addAttribute("page", pageOptional.get());
            return "admin/page/editPage";
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PostMapping("/edytuj")
    public String updatePage(Page page) {
        pageService.update(page);
        return "redirect:/admin/strony";
    }


}
