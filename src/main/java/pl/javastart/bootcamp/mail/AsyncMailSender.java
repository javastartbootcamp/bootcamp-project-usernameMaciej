package pl.javastart.bootcamp.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.javastart.bootcamp.config.JavaStartProperties;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class AsyncMailSender {

    private final Logger logger = LoggerFactory.getLogger(AsyncMailSender.class);

    private JavaMailSenderImpl javaMailSender;
    private MailProperties mailProperties;
    private JavaStartProperties javaStartProperties;

    public AsyncMailSender(JavaMailSenderImpl javaMailSender, MailProperties mailProperties, JavaStartProperties javaStartProperties) {
        this.javaMailSender = javaMailSender;
        this.mailProperties = mailProperties;
        this.javaStartProperties = javaStartProperties;
    }

    @Async
    public void sendEmail(String to, String subject, String content) {
        sendEmailWithAttachment(to, subject, content, null, null);
    }

    @Async
    public void sendEmailWithAttachment(String to, String subject, String content, String attachmentName, File attachment) {
        logger.warn("Send e-mail[html ] to '{}' with subject '{}'", to, subject);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            String fromEmail = "noreply@javastart.pl";
            String fromName = "JavaStart Bootcamp";
            String replyTo = "bootcamp@javastart.pl";

            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(to);
            message.setFrom(fromEmail, fromName);
            message.setSubject(subject);
            message.setReplyTo(replyTo);
            message.setText(content, true);
            if (attachmentName != null) {
                message.addAttachment(attachmentName, attachment);
            }

            javaMailSender.send(mimeMessage);


            try {
                saveEmailToSendDir(mimeMessage);
            } catch (Exception e) {
                logger.warn("E-mail could not be saved to send dir, exception is: {}", e.getMessage());
            }

            logger.warn("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            logger.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    private void saveEmailToSendDir(MimeMessage mimeMessage) throws MessagingException {
        Session session = javaMailSender.getSession();

        Store store = session.getStore("imap");
        String host = javaStartProperties.getSendboxHost();
        String username = javaStartProperties.getSendboxUsername();
        String password = javaStartProperties.getSendboxPassword();

        store.connect(host, username, password);

        Folder folder = store.getFolder("Sent");
        folder.open(Folder.READ_WRITE);
        mimeMessage.setFlag(Flags.Flag.SEEN, true);
        folder.appendMessages(new Message[]{mimeMessage});

        store.close();
    }
}
