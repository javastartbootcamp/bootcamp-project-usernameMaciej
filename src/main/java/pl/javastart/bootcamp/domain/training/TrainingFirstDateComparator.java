package pl.javastart.bootcamp.domain.training;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class TrainingFirstDateComparator implements Comparator<Training> {

    @Override
    public int compare(Training o1, Training o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }

        String[] o1DatesArray = o1.getDates().split(", ");
        String[] o2DatesArray = o2.getDates().split(", ");

        if (o1DatesArray.length == 0) {
            return 1;
        }
        if (o2DatesArray.length == 0) {
            return -1;
        }

        LocalDate o1FirstDate = LocalDate.parse(o1DatesArray[0], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalDate o2FirstDate = LocalDate.parse(o2DatesArray[0], DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        return o1FirstDate.compareTo(o2FirstDate);
    }
}
