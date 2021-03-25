package pl.javastart.bootcamp.domain.file;

import org.springframework.stereotype.Service;
import pl.javastart.bootcamp.config.JavaStartProperties;

import java.io.File;

@Service
public class FileController {

    private JavaStartProperties javaStartProperties;

    public FileController(JavaStartProperties javaStartProperties) {
        this.javaStartProperties = javaStartProperties;
    }

    public void removeAgreementFile(String filename) {
        File file = new File(javaStartProperties.getAgreementsDir(), filename);
        try {
            file.delete();
        } catch (Exception e) {
            System.out.println("Nie udało się usunąć pliku " + filename);
        }

    }
}
