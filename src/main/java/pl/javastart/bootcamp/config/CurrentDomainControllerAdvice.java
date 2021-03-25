package pl.javastart.bootcamp.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentDomainControllerAdvice {

    private final JavaStartProperties javaStartProperties;

    public CurrentDomainControllerAdvice(JavaStartProperties javaStartProperties) {
        this.javaStartProperties = javaStartProperties;
    }

    @ModelAttribute("currentDomain")
    public String currentDomain() {
        return javaStartProperties.getFullDomainAddress();
    }

}
