package pl.javastart.bootcamp.domain.admin.signup;

import lombok.Data;
import pl.javastart.bootcamp.domain.signup.SignupStatus;

@Data
public class ManualTrainingSignupDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Long trainingId;
    private SignupStatus signupStatus;
}
