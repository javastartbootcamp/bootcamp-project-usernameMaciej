package pl.javastart.bootcamp.domain.document;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findByName(String agreement);
}
