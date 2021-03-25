package pl.javastart.bootcamp.domain.signup;

public enum SignupStatus {
    NEW("Utworzony"),
    APPROVED("Zatwierdzone"),
    RESERVE("Lista rezerwowa"),
    AGREEMENT_SIGNED("Umowa podpisana"),
    ADVANCE_PAID("Zadatek opłacony"),
    PAID("Opłacone w całości"),
    REJECTED("Odrzucony");

    private String text;

    SignupStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
