package pl.javastart.bootcamp.domain.signup.log;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.domain.signup.Signup;

import java.time.LocalDateTime;

@Service
public class SignupLogItemService {

    private SignupLogItemRepository signupLogItemRepository;

    public SignupLogItemService(SignupLogItemRepository signupLogItemRepository) {
        this.signupLogItemRepository = signupLogItemRepository;
    }

    public void addNote(Signup signup, String text) {
        SignupLogItem signupLogItem = new SignupLogItem();
        signupLogItem.setSignup(signup);
        signupLogItem.setItemDate(LocalDateTime.now());
        signupLogItem.setNote(text);
        signupLogItemRepository.save(signupLogItem);
    }
}
