package pl.javastart.bootcamp.domain.page;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String metaDescription;
}
