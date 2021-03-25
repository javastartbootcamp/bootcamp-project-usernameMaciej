package pl.javastart.bootcamp.domain.file;

import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.javastart.bootcamp.config.JavaStartProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class FileDownloadController {

    private final JavaStartProperties javaStartProperties;

    public FileDownloadController(JavaStartProperties javaStartProperties) {
        this.javaStartProperties = javaStartProperties;
    }

    @GetMapping(value = "/files/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {

        File targetFile = new File(javaStartProperties.getFilesDir() + "/" + filename);
        if (targetFile.exists()) {
            return ResponseEntity.ok(IOUtils.toByteArray(new FileInputStream(targetFile)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
