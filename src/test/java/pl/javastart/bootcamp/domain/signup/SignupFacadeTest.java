package pl.javastart.bootcamp.domain.signup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.javastart.bootcamp.domain.agreement.AgreementService;
import pl.javastart.bootcamp.domain.signup.log.SignupLogItemService;
import pl.javastart.bootcamp.domain.training.Training;
import pl.javastart.bootcamp.domain.user.UserService;
import pl.javastart.bootcamp.mail.MailService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SignupFacadeTest {

    @Mock private MailService mailService;
    @Mock private SignupService signupService;
    @Mock private AgreementService agreementService;
    @Mock private UserService userService;
    @Mock private SignupLogItemService signupLogItemService;

    @InjectMocks private SignupFacade signupFacade;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void reject_remove_from_reserve_list() {
        // given
        Long signupToRejectId = 11L;

        Signup signup = new Signup();
        signup.setId(signupToRejectId);
        Training trainingWithNSignups = createTrainingWithNSignups(12);
        trainingWithNSignups.setMaxAttendees(10);
        signup.setTraining(trainingWithNSignups);
        when(signupService.findById(signupToRejectId)).thenReturn(Optional.of(signup));

        // when
        signupFacade.rejectSignup(signupToRejectId, "Brak kontaktu");

        // then
        ArgumentCaptor<Signup> signupCaptor = ArgumentCaptor.forClass(Signup.class);
        ArgumentCaptor<Integer> placeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mailService, times(1)).sendReservePlaceChangedEmail(signupCaptor.capture(), placeCaptor.capture());

        assertThat(placeCaptor.getValue()).isEqualTo(1);
    }

    @Test
    public void reject_remove_from_reserve_list_2() {
        // given
        Long signupToRejectId = 11L;

        Signup signup = new Signup();
        signup.setId(signupToRejectId);
        Training trainingWithNSignups = createTrainingWithNSignups(13);
        trainingWithNSignups.setMaxAttendees(10);
        signup.setTraining(trainingWithNSignups);
        when(signupService.findById(signupToRejectId)).thenReturn(Optional.of(signup));

        // when
        signupFacade.rejectSignup(signupToRejectId, "Brak kontaktu");

        // then
        ArgumentCaptor<Signup> signupCaptor = ArgumentCaptor.forClass(Signup.class);
        ArgumentCaptor<Integer> placeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mailService, times(2)).sendReservePlaceChangedEmail(signupCaptor.capture(), placeCaptor.capture());

        assertThat(signupCaptor.getAllValues().get(0).getId()).isEqualTo(12);
        assertThat(placeCaptor.getAllValues().get(0)).isEqualTo(1);
        assertThat(signupCaptor.getAllValues().get(1).getId()).isEqualTo(13);
        assertThat(placeCaptor.getAllValues().get(1)).isEqualTo(2);
    }

    @Test
    public void reject_remove_from_middle_reserve_list() {
        // given
        Long signupToRejectId = 12L;

        Signup signup = new Signup();
        signup.setId(signupToRejectId);
        Training trainingWithNSignups = createTrainingWithNSignups(13);
        trainingWithNSignups.setMaxAttendees(10);
        signup.setTraining(trainingWithNSignups);
        when(signupService.findById(signupToRejectId)).thenReturn(Optional.of(signup));

        // when
        signupFacade.rejectSignup(signupToRejectId, "Brak kontaktu");

        // then
        ArgumentCaptor<Signup> signupCaptor = ArgumentCaptor.forClass(Signup.class);
        ArgumentCaptor<Integer> placeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mailService, times(1)).sendReservePlaceChangedEmail(signupCaptor.capture(), placeCaptor.capture());

        // 13 -> 12 (1st on reserve list)
        assertThat(signupCaptor.getValue().getId()).isEqualTo(13);
        assertThat(placeCaptor.getValue()).isEqualTo(2);
    }

    @Test
    public void reject_remove_first_on_list() {
        // given
        Long signupToRejectId = 1L;

        Signup signup = new Signup();
        signup.setId(signupToRejectId);
        Training trainingWithNSignups = createTrainingWithNSignups(13);
        trainingWithNSignups.setMaxAttendees(10);
        signup.setTraining(trainingWithNSignups);
        when(signupService.findById(signupToRejectId)).thenReturn(Optional.of(signup));

        // when
        signupFacade.rejectSignup(signupToRejectId, "Brak kontaktu");

        // then
        ArgumentCaptor<Signup> freePlace = ArgumentCaptor.forClass(Signup.class);
        verify(mailService).sendPlaceFreeMail(freePlace.capture());
        assertThat(freePlace.getValue().getId()).isEqualTo(11L);

        ArgumentCaptor<Signup> signupCaptor = ArgumentCaptor.forClass(Signup.class);
        ArgumentCaptor<Integer> placeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mailService, times(2)).sendReservePlaceChangedEmail(signupCaptor.capture(), placeCaptor.capture());

        // 13 -> 12
        // 12 -> 11

        assertThat(placeCaptor.getAllValues().get(0)).isEqualTo(1);
        assertThat(signupCaptor.getAllValues().get(0).getId()).isEqualTo(12);
        assertThat(placeCaptor.getAllValues().get(1)).isEqualTo(2);
        assertThat(signupCaptor.getAllValues().get(1).getId()).isEqualTo(13);
    }

    private Training createTrainingWithNSignups(int n) {
        Training training = new Training();
        List<Signup> signups = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Signup signup = new Signup();
            signup.setId((long) i + 1);
            signups.add(signup);
        }
        training.setSignups(signups);
        return training;
    }

}