package pl.javastart.bootcamp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import javax.annotation.PostConstruct;

@Configuration
public class ThymeleafExtension {

    private final SpringTemplateEngine templateEngine;

    @Autowired
    public ThymeleafExtension(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @PostConstruct
    public void extension() {
        templateEngine.addTemplateResolver(new StringTemplateResolver());
    }
}
