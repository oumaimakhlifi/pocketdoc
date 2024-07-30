package pocketDock.com.pocketDock.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;


import java.sql.Date; // Importez java.sql.Date pour représenter la date sans l'heure

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Convention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idConv;

    private Date dateDebut; // Utilisez java.sql.Date pour représenter la date sans l'heure

    private Date dateFin; // Utilisez java.sql.Date pour représenter la date sans l'heure
    private String description;
    @Column(name = "reduction_frais_consultation")
    private double reductionFraisConsultation;

    @Column(name = "services_inclus")
    private String servicesInclus;
    private String cin;
    @Column(name = "conditions_resiliation")
    private String conditionsResiliation;
    @JsonIgnore
    @OneToOne(mappedBy = "convention" )
    @Where(clause = "role = 'DOCTOR'")
    private OurUsers med;
}
