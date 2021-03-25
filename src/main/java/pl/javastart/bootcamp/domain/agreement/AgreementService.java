package pl.javastart.bootcamp.domain.agreement;

import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.javastart.bootcamp.config.JavaStartProperties;
import pl.javastart.bootcamp.domain.document.Document;
import pl.javastart.bootcamp.domain.document.DocumentRepository;
import pl.javastart.bootcamp.domain.signup.Signup;
import pl.javastart.bootcamp.domain.user.User;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class AgreementService {

    private final DocumentRepository documentRepository;
    private final SpringTemplateEngine springTemplateEngine;
    private final JavaStartProperties javaStartProperties;
    private final PdfGenerator pdfGenerator;

    public AgreementService(DocumentRepository documentRepository,
                            SpringTemplateEngine springTemplateEngine,
                            JavaStartProperties javaStartProperties,
                            PdfGenerator pdfGenerator) {
        this.documentRepository = documentRepository;
        this.springTemplateEngine = springTemplateEngine;
        this.javaStartProperties = javaStartProperties;
        this.pdfGenerator = pdfGenerator;
    }

    public String prepareTemplateAgreementForSignup(Signup signup) {
        return prepareAgreement(signup, false, LocalDate.now());
    }

    public String prepareSignedAgreementForSignup(Signup signup, LocalDate today) {
        if (signup.getAgreementSignedFileName() != null) {
            return signup.getAgreementSignedFileName();
        }
        return prepareAgreement(signup, true, today);
    }

    private String prepareAgreement(Signup signup, boolean isSigned, LocalDate today) {
        User user = signup.getUser();

        Context context = new Context();
        context.setVariable("isSigned", isSigned);
        context.setVariable("user", user);
        context.setVariable("signup", signup);
        context.setVariable("company", signup.getTraining().getCompany());
        context.setVariable("customPaymentInfo", signup.getCustomPaymentInfo());

        boolean showDepositInformation = false;
        LocalDate advancePaymentTo = signup.getAdvancePaymentToOverride();
        if (advancePaymentTo == null) {
            advancePaymentTo = signup.getAdvancePaymentTo();
            if (signup.getAdvancePaymentTo().isBefore(signup.getTraining().getFullPaymentFrom())) {
                showDepositInformation = true;
            }
        } else {
            showDepositInformation = true;
        }

        boolean showFullPaymentFrom = false;
        LocalDate fullPaymentFrom = null;
        if (signup.getFullPaymentFromOverride() != null) {
            fullPaymentFrom = signup.getFullPaymentFromOverride();
            showFullPaymentFrom = true;
        } else if (signup.getTraining().getFullPaymentFrom().isAfter(today)) {
            fullPaymentFrom = signup.getTraining().getFullPaymentFrom();
            showFullPaymentFrom = true;
        }

        LocalDate fullPaymentTo = signup.getTraining().getFullPaymentTo();
        if (signup.getFullPaymentToOverride() != null) {
            fullPaymentTo = signup.getFullPaymentToOverride();
        } else {
            if (fullPaymentTo.isBefore(today)) {
                fullPaymentTo = today.plusDays(3);
            }
        }

        context.setVariable("showDepositInformation", showDepositInformation);
        context.setVariable("advancePaymentTo", advancePaymentTo);
        context.setVariable("showFullPaymentFrom", showFullPaymentFrom);
        context.setVariable("fullPaymentFrom", fullPaymentFrom);
        context.setVariable("fullPaymentTo", fullPaymentTo);

        Document agreement = documentRepository.findByName("agreement");
        String htmlAgreement = springTemplateEngine.process(agreement.getContent(), context);

        String uuid = UUID.randomUUID().toString();
        String filename = uuid + ".pdf";
        File dir = new File(javaStartProperties.getAgreementsDir());
        dir.mkdirs();
        File file = new File(dir, filename);

        try {
            pdfGenerator.generateAgreement(htmlAgreement, file);
            return filename;
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAgreementContent() {
        return documentRepository.findByName("agreement").getContent();
    }

    public void updateAgreement(String content) {
        Document agreement = documentRepository.findByName("agreement");
        agreement.setContent(content);
        documentRepository.save(agreement);
    }
}
