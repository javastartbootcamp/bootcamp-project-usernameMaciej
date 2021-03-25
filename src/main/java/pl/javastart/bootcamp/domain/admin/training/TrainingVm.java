package pl.javastart.bootcamp.domain.admin.training;

import pl.javastart.bootcamp.domain.signup.SignupStatus;
import pl.javastart.bootcamp.domain.training.Training;

public class TrainingVm {

    private Long id;
    private String title;
    private String code;
    private String type;
    private long activeSignupsCount;
    private String firstDate;

    static TrainingVm toVm(Training training) {
        TrainingVm trainingVm = new TrainingVm();
        trainingVm.id = training.getId();
        trainingVm.title = training.getDescription().getTitle();
        trainingVm.code = training.getCode();
        trainingVm.type = training.getType();
        trainingVm.activeSignupsCount = training.getSignups().stream().filter(s -> s.getStatus() != SignupStatus.REJECTED).count();
        trainingVm.firstDate = training.getDates().split(", ")[0];
        return trainingVm;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public long getActiveSignupsCount() {
        return activeSignupsCount;
    }

    public String getFirstDate() {
        return firstDate;
    }
}
