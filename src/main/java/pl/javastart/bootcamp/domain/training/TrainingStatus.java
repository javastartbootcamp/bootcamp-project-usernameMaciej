package pl.javastart.bootcamp.domain.training;

public enum TrainingStatus {
    PLANNED("Planowane", true),
    IN_PROGRESS("W trakcie", true),
    CLOSED("Zamknięte", false),
    CANCELLED("Anulowane", false);

    private String text;
    private boolean isActive;

    TrainingStatus(String text, boolean isActive) {
        this.text = text;
        this.isActive = isActive;
    }

    public String getText() {
        return text;
    }

    public boolean isActive() {
        return isActive;
    }
}
