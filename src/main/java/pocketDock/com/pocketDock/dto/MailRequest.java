package pocketDock.com.pocketDock.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MailRequest {
    private String to; // Adresse e-mail du destinataire
    private String subject; // Sujet de l'e-mail
    private String doctorName; // Nom du médecin
    private LocalDate expirationDate; // Date d'expiration de la convention
}
