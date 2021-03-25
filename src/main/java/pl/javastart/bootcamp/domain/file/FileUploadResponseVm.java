package pl.javastart.bootcamp.domain.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResponseVm {

    private String status;
    private String message;
    private String imageUrl;
}
