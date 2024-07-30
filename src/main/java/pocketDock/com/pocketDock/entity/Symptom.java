package pocketDock.com.pocketDock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "symptoms")

public class Symptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Intense severity;

    private String description;

    private String location;

    private String duration;


    private String triggers;

    @ManyToOne
    @JoinColumn(name = "fiche_id")
    @JsonIgnore
    private Fiche myFiche;

    private int seenByDoctor;

    // Injecter le JavaMailSender pour l'envoi d'email
    public Fiche getMyFiche() {
        return myFiche;
    }


    public Long getFicheId() {
        if (myFiche != null) {
            return myFiche.getIdFiche();
        } else {
            return null;
        }
    }
}


