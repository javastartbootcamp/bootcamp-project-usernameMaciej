package pl.javastart.bootcamp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import pl.javastart.bootcamp.domain.admin.training.AdminTrainingController;
import pl.javastart.bootcamp.domain.signup.SignupController;
import pl.javastart.bootcamp.domain.signup.SignupDto;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.training.TrainingService;
import pl.javastart.bootcamp.domain.training.description.TrainingDescription;
import pl.javastart.bootcamp.domain.training.description.TrainingDescriptionService;
import pl.javastart.bootcamp.mail.AsyncMailSender;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class WorkflowTest {

    @Autowired AdminTrainingController adminTrainingController;
    @Autowired TrainingDescriptionService trainingDescriptionService;
    @Autowired TrainingService trainingService;
    @Autowired SignupController signupController;

    @MockBean AsyncMailSender asyncMailSender;
    @Mock private BindingResult noErrorsBindingResult;
    @Mock private Model model;

    @Before
    public void init() {
        when(noErrorsBindingResult.hasErrors()).thenReturn(false);
    }

    @Test
    public void workflow1() {
        Long trainingId = adminCreatesTraining();
        firstSignup(trainingId);
        secondSignup(trainingId);
        thirdSignup(trainingId);

    }

    private Long adminCreatesTraining() {
        TrainingDescription trainingDescription = trainingDescriptionService.findByUrl("junior-java-developer-online").orElse(null);
        Training training = new Training();
        training.setCode("jjd-2019-10");
        training.setMinAttendees(2);
        training.setMaxAttendees(3);
        training.setHourFrom(LocalTime.of(17, 0));
        training.setHourFrom(LocalTime.of(20, 0));
        training.setDates("08.10.2019, 09.10.2019");
        training.setDescription(trainingDescription);
        training.setHoursDescription("Szkolenie w pon i pt od 17 do 20");
        training.setType("Wieczorowe");
        training.setDeposit(BigDecimal.valueOf(1000));

        adminTrainingController.addTraining(training);

        List<Training> all = trainingService.findAll();
        return all.get(all.size() - 1).getId();
    }

    private void firstSignup(Long trainingId) {
        SignupDto signupDto = new SignupDto();
        signupDto.setTrainingId(trainingId);
        signupDto.setEmail("javastarttesting1@byom.de");
        signupDto.setFirstName("Jan");
        signupDto.setLastName("Kowalski");
        signupDto.setStreet("Kościelna");
        signupDto.setHouseNumber("12");
        signupDto.setFlatNumber("3");
        signupDto.setPostalCode("50-555");
        signupDto.setCity("Wrocław");
        signupDto.setMessage("Potrzebuję laptopa");
        signupDto.setPhoneNumber("680 806 531");
        signupDto.setAcceptTerms(true);

        signupController.signup(signupDto, noErrorsBindingResult, model);
        ArgumentCaptor<String> emailArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(asyncMailSender, atLeastOnce()).sendEmail(emailArgCaptor.capture(), titleArgCaptor.capture(), contentArgCaptor.capture());
        assertThat(emailArgCaptor.getAllValues().get(0)).isEqualTo("javastarttesting1@byom.de");
        assertThat(titleArgCaptor.getAllValues().get(0)).isEqualTo("Wymagane potwierdzenie maila do zapisu na Bootcamp Junior Java Developer");
        assertThat(contentArgCaptor.getAllValues().get(0)).contains("Dziękujemy za zapis na Bootcamp Junior Java Developer. Potwierdź proszę ten adres email klikając w link poniżej.");
        assertThat(emailArgCaptor.getAllValues().get(1)).isEqualTo("javastarttester@gmail.com");
        assertThat(titleArgCaptor.getAllValues().get(1)).isEqualTo("Zapis na szkolenie jjd-2019-10");
        Mockito.reset(asyncMailSender);
    }

    private void secondSignup(Long trainingId) {
        SignupDto signupDto = new SignupDto();
        signupDto.setTrainingId(trainingId);
        signupDto.setEmail("javastarttesting2@byom.de");
        signupDto.setFirstName("Anna");
        signupDto.setLastName("Nowak");
        signupDto.setStreet("Kościelna");
        signupDto.setHouseNumber("12");
        signupDto.setFlatNumber("3");
        signupDto.setPostalCode("50-555");
        signupDto.setCity("Wrocław");
        signupDto.setMessage("Potrzebuję laptopa");
        signupDto.setPhoneNumber("680 806 531");
        signupDto.setAcceptTerms(true);

        signupController.signup(signupDto, noErrorsBindingResult, model);
        ArgumentCaptor<String> emailArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(asyncMailSender, atLeastOnce()).sendEmail(emailArgCaptor.capture(), titleArgCaptor.capture(), contentArgCaptor.capture());
        assertThat(emailArgCaptor.getAllValues().get(0)).isEqualTo("javastarttesting2@byom.de");
        assertThat(titleArgCaptor.getAllValues().get(0)).isEqualTo("Wymagane potwierdzenie maila do zapisu na Bootcamp Junior Java Developer");
        assertThat(contentArgCaptor.getAllValues().get(0)).contains("Dziękujemy za zapis na Bootcamp Junior Java Developer. Potwierdź proszę ten adres email klikając w link poniżej.");
        assertThat(emailArgCaptor.getAllValues().get(1)).isEqualTo("javastarttester@gmail.com");
        assertThat(titleArgCaptor.getAllValues().get(1)).isEqualTo("Zapis na szkolenie jjd-2019-10");
        Mockito.reset(asyncMailSender);
    }

    private void thirdSignup(Long trainingId) {
        SignupDto signupDto = new SignupDto();
        signupDto.setTrainingId(trainingId);
        signupDto.setEmail("javastarttesting3@byom.de");
        signupDto.setFirstName("Anna");
        signupDto.setLastName("Nowak");
        signupDto.setStreet("Kościelna");
        signupDto.setHouseNumber("12");
        signupDto.setFlatNumber("3");
        signupDto.setPostalCode("50-555");
        signupDto.setCity("Wrocław");
        signupDto.setMessage("Potrzebuję laptopa");
        signupDto.setPhoneNumber("680 806 531");
        signupDto.setAcceptTerms(true);

        signupController.signup(signupDto, noErrorsBindingResult, model);
        ArgumentCaptor<String> emailArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(asyncMailSender, atLeastOnce()).sendEmail(emailArgCaptor.capture(), titleArgCaptor.capture(), contentArgCaptor.capture());
        assertThat(emailArgCaptor.getAllValues().get(0)).isEqualTo("javastarttesting3@byom.de");
        assertThat(titleArgCaptor.getAllValues().get(0)).isEqualTo("Wymagane potwierdzenie maila do zapisu na Bootcamp Junior Java Developer");
        assertThat(contentArgCaptor.getAllValues().get(0)).contains("Dziękujemy za zapis na Bootcamp Junior Java Developer. Potwierdź proszę ten adres email klikając w link poniżej.");
        assertThat(emailArgCaptor.getAllValues().get(1)).isEqualTo("javastarttester@gmail.com");
        assertThat(titleArgCaptor.getAllValues().get(1)).isEqualTo("Zapis na szkolenie jjd-2019-10");
        Mockito.reset(asyncMailSender);
    }

}
