package pl.javastart.bootcamp.domain.admin;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import pl.javastart.bootcamp.domain.training.Training;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AgreementTestData {

    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;
    private String city;
    private String email;
    private String phoneNumber;
    private Training training;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate today = LocalDate.now();

    private BigDecimal deposit;
    private BigDecimal price;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate advancePaymentTo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fullPaymentFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fullPaymentTo;

}
