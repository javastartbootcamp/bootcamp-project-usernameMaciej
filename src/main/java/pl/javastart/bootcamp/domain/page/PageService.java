package pl.javastart.bootcamp.domain.page;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PageService {

    private PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Optional<Page> findByUrl(String url) {
        return pageRepository.findByUrl(url);
    }

    public List<Page> findAll() {
        return pageRepository.findAll();
    }

    public Optional<Page> findById(Long id) {
        return pageRepository.findById(id);
    }

    public Page insert(Page page) {
        if (page.getId() != null) {
            throw new IllegalArgumentException("ID should be null when adding");
        }
        return pageRepository.save(page);
    }

    public Page update(Page page) {
        if (page.getId() == null) {
            throw new IllegalArgumentException("ID should be not be null when updating");
        }
        return pageRepository.save(page);
    }
}
