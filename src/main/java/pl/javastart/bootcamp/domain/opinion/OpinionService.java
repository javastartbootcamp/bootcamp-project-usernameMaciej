package pl.javastart.bootcamp.domain.opinion;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpinionService {

    private OpinionRepository opinionRepository;

    public OpinionService(OpinionRepository opinionRepository) {
        this.opinionRepository = opinionRepository;
    }

    public List<Opinion> findAll() {
        return opinionRepository.findAll();
    }

    public Optional<Opinion> findById(Long id) {
        return opinionRepository.findById(id);
    }

    public Opinion insert(Opinion opinion) {
        if (opinion.getId() != null) {
            throw new IllegalArgumentException("ID should be null when adding");
        }
        return opinionRepository.save(opinion);
    }

    public Opinion update(Opinion opinion) {
        if (opinion.getId() == null) {
            throw new IllegalArgumentException("ID should be not be null when updating");
        }
        return opinionRepository.save(opinion);
    }
}
