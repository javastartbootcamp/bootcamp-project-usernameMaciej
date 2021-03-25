package pl.javastart.bootcamp.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BigDecimalFormatter {

    public String convertDecimalToString(BigDecimal num) {
        if (num == null) {
            return "";
        }
        String result;
        try {
            result = num.toBigIntegerExact().toString();
        } catch (ArithmeticException e) {
            num = num.setScale(1, RoundingMode.HALF_UP);
            result = num.toPlainString();
        }
        return result;
    }
}
