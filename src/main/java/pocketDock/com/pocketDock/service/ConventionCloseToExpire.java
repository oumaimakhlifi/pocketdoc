package pocketDock.com.pocketDock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pocketDock.com.pocketDock.dto.MailRequest;
import pocketDock.com.pocketDock.entity.Convention;
import pocketDock.com.pocketDock.repository.ConventionRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConventionCloseToExpire {

    @Autowired
    private ConventionRepository conventionRepository;

    @Autowired
    private EmailService emailService;

    private boolean renewalSuccess; // Variable pour suivre l'état du renouvellement

    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndSendExpirationEmails() {
        List<Convention> conventions = conventionRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        for (Convention convention : conventions) {
            LocalDate expirationMinusOneWeek = convention.getDateFin().toLocalDate().minusWeeks(1);

            if (currentDate.isEqual(expirationMinusOneWeek) || currentDate.isAfter(expirationMinusOneWeek)) {
                sendReminderEmail(convention);
            }
        }
    }

    public void sendReminderEmail(Convention convention) {
        // Adresse e-mail du médecin associé à la convention
        String to = convention.getMed().getEmail();

        // Création de la requête e-mail
        MailRequest request = new MailRequest();
        request.setTo(to);
        request.setSubject("Rappel de renouvellement de convention");
        Date dateFin = convention.getDateFin(); // Supposons que convention.getDateFin() retourne un objet Date

        // Convertir Date en LocalDate
        LocalDate localDateFin = dateFin.toLocalDate();

        // Maintenant, vous pouvez définir la date d'expiration dans votre objet MailRequest
        request.setExpirationDate(localDateFin);
        // Création du modèle pour le contenu de l'e-mail
        Map<String, Object> model = new HashMap<>();
        model.put("doctorName", convention.getMed().getName() + " " + convention.getMed().getLastname());
        model.put("expirationDate", request.getExpirationDate());
        // Envoi de l'e-mail en utilisant le service EmailService
        emailService.sendEmail(request, model);
    }

    public boolean renewConvention() {
        List<Convention> conventions = conventionRepository.findAll();
        LocalDate currentDate = LocalDate.now();
        for (Convention convention : conventions) {
            LocalDate expirationMinusOneWeek = convention.getDateFin().toLocalDate().minusWeeks(1);
            if (currentDate.isEqual(expirationMinusOneWeek) || currentDate.isAfter(expirationMinusOneWeek)) {
                // Tentative de renouvellement de la convention
                if (performRenewal(convention)) {
                    renewalSuccess = true;
                    return true;
                } else {
                    renewalSuccess = false;
                    return false;
                }
            }
        }
        return false; // Aucune convention à renouveler
    }

    private boolean performRenewal(Convention convention) {
        // Convertir la date de fin de type java.sql.Date en LocalDate
        LocalDate endDate = convention.getDateFin().toLocalDate();

        // Mettre à jour la date de fin de la convention en ajoutant un an
        LocalDate newEndDate = endDate.plusYears(1);

        // Convertir la nouvelle date de fin en java.sql.Date
        Date newEndDateAsSqlDate = Date.valueOf(newEndDate);

        // Mettre à jour la convention avec la nouvelle date de fin
        convention.setDateFin(newEndDateAsSqlDate);
        conventionRepository.save(convention);

        return true; // Exemple de réussite
    }

    public boolean isRenewalSuccess() {
        return renewalSuccess;
    }
}