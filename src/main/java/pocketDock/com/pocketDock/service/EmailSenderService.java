package pocketDock.com.pocketDock.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.entity.Symptom;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Service
public class EmailSenderService {


        @Autowired
        private JavaMailSender mailSender;

        private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class);
    LocalDateTime now = LocalDateTime.now();

    // Formater la date et l'heure selon vos besoins
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    String formattedDateTime = now.format(formatter);
        public void sendHighSeveritySymptomEmail(Symptom symptom,Fiche fiche) {
            if ( symptom.getSeverity().toString() =="HIGH") {

                try {

                    OurUsers etu=fiche.getEtudiant();
                    OurUsers psy=fiche.getPsy();



                    // Récupérer l'e-mail du médecin (psy) associé à la fiche
                    String doctorEmail = psy.getEmail();

                    // Créer le message d'e-mail
                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setFrom("azizyomna4@gmail.com");
                    mailMessage.setTo(doctorEmail); // Adresse du médecin (psy)
                    mailMessage.setSubject("Alerte de gravité du symptôme");
                    mailMessage.setText("Cher MR/MME  "+psy.getName()+" "+psy.getLastname()+
                            "\nUn symptôme de gravité élevée a été soumis. \nDétails : " +
                            "\n     Un symptome était envoyé le "+ formattedDateTime+" de la part de : "+etu.getName()+", de type : "+symptom.getType()
                            +", causant du mal dans : "+symptom.getLocation()+", possédant une durée de : "+symptom.getDuration()
                            +", facteurs déclencheurs : "+symptom.getTriggers()
                            +", description plus détaillée : " +symptom.getDescription()+" ."
                            +"\n veuiller retourner au plateforme pour plus d'informations " +".");

                    // Envoyer l'e-mail
                    mailSender.send(mailMessage);
                    logger.info("Mail sent successfully");
                } catch (Exception e) {
                    // Gérer les erreurs lors de l'envoi de l'e-mail
                    logger.error("Failed to send high severity symptom email: {}", e.getMessage());
                }
            } else {
                // Gérer le cas où l'utilisateur associé à la fiche est null ou n'est pas un médecin
                logger.warn("Failed to send email: No doctor associated with the fiche or symptom severity is not high.");
            }
        }




    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Indiquez que le texte est au format HTML
            mailSender.send(message);
            System.out.println("E-mail envoyé avec succès !");
        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }




}
