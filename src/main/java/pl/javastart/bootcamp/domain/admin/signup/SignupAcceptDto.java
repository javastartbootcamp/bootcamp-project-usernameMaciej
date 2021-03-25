package pl.javastart.bootcamp.domain.admin.signup;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SignupAcceptDto {

    private Long signupId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;
    private String postalCode;
    private String city;
    private BigDecimal deposit;
    private BigDecimal price;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate advancePaymentTo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fullPaymentFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fullPaymentTo;
    private boolean advancePaymentToDefault = true;
    private boolean fullPaymentFromDefault = true;
    private boolean fullPaymentToDefault = true;
    private String customPaymentInfo;

}
