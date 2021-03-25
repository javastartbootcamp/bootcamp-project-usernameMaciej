package pl.javastart.bootcamp.domain.agreement;

import javax.validation.constraints.AssertTrue;

public class AgreementAcceptationDto {

    @AssertTrue
    private Boolean accepted;

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
