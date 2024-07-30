package pocketDock.com.pocketDock.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import pocketDock.com.pocketDock.dto.MailRequest;
import pocketDock.com.pocketDock.dto.MailResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender sender;
    @Autowired
    @Qualifier("factoryBean")
    private Configuration config;
    private static final String FROM_EMAIL = "azizyomna4@gmail.com";

    public MailResponse sendEmail(MailRequest request, Map<String, Object> model) {
        MailResponse response = new MailResponse();
        MimeMessage message = sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.addAttachment("img.png", new ClassPathResource("/images/img.png"));

            Template template = config.getTemplate("email-template.ftl");
            String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            helper.setTo(request.getTo());
            helper.setText(htmlBody, true);

            helper.setSubject(request.getSubject());

            // Utilisation de l'adresse e-mail de l'expéditeur fixe
            helper.setFrom(FROM_EMAIL);

            sender.send(message);

            response.setMessage("Email envoyé à : " + request.getTo());
            response.setStatus(Boolean.TRUE);

        } catch (MessagingException | IOException | TemplateException e) {
            response.setMessage("Échec de l'envoi de l'email : " + e.getMessage());
            response.setStatus(Boolean.FALSE);
        }
        return response;
    }

}
