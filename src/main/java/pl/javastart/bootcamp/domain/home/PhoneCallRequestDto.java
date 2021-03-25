package pl.javastart.bootcamp.domain.home;

import lombok.Data;

@Data
public class PhoneCallRequestDto {
    private String firstName;
    private String phoneNumber;
    private String contactDate;
}
