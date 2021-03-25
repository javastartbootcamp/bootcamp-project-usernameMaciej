package pl.javastart.bootcamp.domain.signup.log;

import pl.javastart.bootcamp.domain.signup.Signup;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SignupLogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime itemDate;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne
    private Signup signup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getItemDate() {
        return itemDate;
    }

    public void setItemDate(LocalDateTime itemDate) {
        this.itemDate = itemDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Signup getSignup() {
        return signup;
    }

    public void setSignup(Signup signup) {
        this.signup = signup;
    }
}
