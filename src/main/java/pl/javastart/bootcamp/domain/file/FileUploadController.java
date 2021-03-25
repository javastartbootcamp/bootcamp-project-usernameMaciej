package pl.javastart.bootcamp.domain.file;

import liquibase.util.file.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.javastart.bootcamp.config.JavaStartProperties;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Controller
public class FileUploadController {

    private final JavaStartProperties javaStartProperties;

    public FileUploadController(JavaStartProperties javaStartProperties) {
        this.javaStartProperties = javaStartProperties;
    }

    @PostMapping("/admin/img/upload")
    public ResponseEntity<FileUploadResponseVm> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                                 RedirectAttributes redirectAttributes) {
        if (file.getOriginalFilename().contains("/")) {
            redirectAttributes.addFlashAttribute("message", "Folder separators not allowed");
            return ResponseEntity.badRequest().body(new FileUploadResponseVm("NOK", "Folder separators not allowed", ""));
        }

        String filename = file.getOriginalFilename().replaceAll(" ", "_");
        if (!file.isEmpty()) {
            try {
                File fileDir = new File(javaStartProperties.getFilesDir());
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                File targetFile = new File(javaStartProperties.getFilesDir() + "/" + filename);
                int counter = 1;
                String firstPartWithoutExtension = FilenameUtils.removeExtension(filename);
                String extension = FilenameUtils.getExtension(filename);
                while (targetFile.exists()) {
                    counter++;
                    String firstPartWithNumber = firstPartWithoutExtension + "-" + counter;
                    filename = extension.isEmpty() ? firstPartWithNumber : firstPartWithNumber + "." + extension;
                    targetFile = new File(javaStartProperties.getFilesDir() + "/" + filename);
                }
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(targetFile));
                FileCopyUtils.copy(file.getInputStream(), stream);
                stream.close();
            } catch (Exception e) {
                return ResponseEntity.status(500).body(new FileUploadResponseVm("NOK", "You failed to upload " + file.getName() + " => " + e.getMessage(), ""));
            }
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new FileUploadResponseVm("NOK", "You failed to upload " + file.getName() + " because the file was empty", ""));
        }

        return ResponseEntity.ok(new FileUploadResponseVm("OK", "", "/files/" + filename));
    }
}
