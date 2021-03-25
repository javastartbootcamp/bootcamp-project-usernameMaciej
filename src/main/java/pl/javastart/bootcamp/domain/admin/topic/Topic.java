package pl.javastart.bootcamp.domain.admin.topic;

import pl.javastart.bootcamp.utils.OrderableItem;

import javax.persistence.*;

@Entity
public class Topic implements OrderableItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String videoLinks;

    @Column(columnDefinition = "TEXT")
    private String lessonLinks;

    private Long sortOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks(String videoLinks) {
        this.videoLinks = videoLinks;
    }

    public String getLessonLinks() {
        return lessonLinks;
    }

    public void setLessonLinks(String lessonLinks) {
        this.lessonLinks = lessonLinks;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }
}
