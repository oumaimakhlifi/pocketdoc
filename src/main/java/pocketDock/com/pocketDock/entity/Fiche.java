package pocketDock.com.pocketDock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fiche {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idFiche;
    private String historiq_fam;
    private String eval_psy;
    private String notes_de_suivi;
    @Enumerated(EnumType.STRING)
    private Type_add type_addic;

     private int etu;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private OurUsers etudiant;

    @ManyToOne
    @JoinColumn(name = "psy_id")
    private OurUsers psy;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "myFiche")
    private Set<Symptom> symptoms;
}
