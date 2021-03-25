package pl.javastart.bootcamp.domain.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;

import java.util.Optional;

@Controller
public class PageController {

    private PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("/strona/{url}")
    public String getPage(@PathVariable String url, Model model) {
        Optional<Page> pageOptional = pageService.findByUrl(url);
        if (pageOptional.isPresent()) {
            model.addAttribute("page", pageOptional.get());
            return "page";
        } else {
            throw new ResourceNotFoundException();
        }
    }

}
