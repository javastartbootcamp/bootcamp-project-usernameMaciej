package pl.javastart.bootcamp.domain.company;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.javastart.bootcamp.config.notfound.ResourceNotFoundException;
import pl.javastart.bootcamp.domain.page.Page;
import pl.javastart.bootcamp.domain.page.PageService;

import java.util.Optional;

@Controller
@RequestMapping("/firmy")
public class CompanyOfferController {

    private static final String OFFER_PAGE_URL = "oferta-dla-firm";

    private PageService pageService;

    public CompanyOfferController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("/oferta")
    public String companyOffer(Model model) {
        Optional<Page> offerPage = pageService.findByUrl(OFFER_PAGE_URL);
        if (offerPage.isPresent()) {
            model.addAttribute("offer", offerPage.get());
            model.addAttribute("title", "Szkolenia z programowania dla Firm - JavaStart");
            model.addAttribute("url", "/oferta");
        } else {
            throw new ResourceNotFoundException();
        }
        return "companyOffer";
    }

    @GetMapping("/szkolenie/{url}")
    public String companyTraining(@PathVariable String url, Model model) {
        Optional<Page> offerPage = pageService.findByUrl(url);
        if (offerPage.isPresent()) {
            Page page = offerPage.get();
            model.addAttribute("offer", page);
            model.addAttribute("singleOffer", true);
            model.addAttribute("title", "Szkolenie " + page.getTitle() + " dla firm - JavaStart");
            model.addAttribute("url", "/szkolenie/" + page.getUrl());
        } else {
            throw new ResourceNotFoundException();
        }
        return "companyOffer";
    }

}
