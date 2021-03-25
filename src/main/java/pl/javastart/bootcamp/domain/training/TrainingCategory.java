package pl.javastart.bootcamp.domain.training;

public enum TrainingCategory {
    DEVELOPER("Programistyczne"),
    TESTER("Testerskie");

    private String text;

    TrainingCategory(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
