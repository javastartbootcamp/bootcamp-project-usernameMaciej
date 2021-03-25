package pl.javastart.bootcamp.domain.user;

import lombok.Getter;
import lombok.Setter;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.user.role.UserRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String phoneNumber;

    private String password;

    private String firstName;

    private String lastName;

    private String address;

    private String postalCode;

    private String city;

    private Boolean activated;

    private String activationCode;

    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "user")
    private List<UserRole> roles;

    @OneToMany(mappedBy = "user")
    private List<Signup> signups;

    private String authKey;

    private String passwordResetKey;

    private String githubUsername;
}
