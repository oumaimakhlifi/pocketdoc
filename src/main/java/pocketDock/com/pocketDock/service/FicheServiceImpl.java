package pocketDock.com.pocketDock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.entity.Symptom;
import pocketDock.com.pocketDock.repository.FicheRepository;
import pocketDock.com.pocketDock.repository.OurUserRepo;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class FicheServiceImpl implements IFicheService{
    @Autowired
    FicheRepository ficheRepository;
        OurUserRepo ourUserRepo;
    @Override
    public List<Fiche> getFiches() {
        return ficheRepository.findAll();

    }

    @Override
    public Fiche retrieveFiche(Long ficheId) {
        return ficheRepository.findById(ficheId).orElse(null);

    }
    public Fiche getLatestFicheByEtudiant(OurUsers etudiant) {
        // Récupérer la liste des fiches correspondant à l'étudiant donné
        List<Fiche> fiches = ficheRepository.getLatestFicheByEtudiant(etudiant);

        // Vérifier si la liste n'est pas vide
        if (!fiches.isEmpty()) {
            // Récupérer la première fiche de la liste (celle avec l'ID le plus grand)
            return fiches.get(0);
        } else {
            // Si la liste est vide, retourner null ou gérer le cas d'erreur selon votre logique métier
            return null;
        }
    }
    @Override
    public Fiche addFiche(Fiche fiche) {
        return ficheRepository.save(fiche);

    }

    @Override
    public void removeFiche(Long ficheId) {
        ficheRepository.deleteById(ficheId);

    }

    public Fiche modifyFiche(Fiche fiche) {
        // Vérifier si la fiche existe dans la base de données
        Fiche existingFiche = ficheRepository.findByIdFiche(fiche.getIdFiche());
        if (existingFiche != null) {
            // Mettre à jour les champs de la fiche existante avec les nouvelles valeurs
            existingFiche.setHistoriq_fam(fiche.getHistoriq_fam());
            existingFiche.setEval_psy(fiche.getEval_psy());
            existingFiche.setNotes_de_suivi(fiche.getNotes_de_suivi());

            // Enregistrer les modifications dans la base de données
            return ficheRepository.save(existingFiche);
        } else {
            // Si la fiche n'est pas trouvée, lancer une exception
            throw new NoSuchElementException("La fiche avec l'ID " + fiche.getIdFiche() + " n'existe pas.");
        }
    }


    @Override
    public Long getNextFicheId() {
        // Implémentez la logique pour récupérer l'ID de la prochaine fiche
        Long lastFicheId = ficheRepository.findMaxFicheId();
        return lastFicheId != null ? lastFicheId + 1 : 1L; // Si aucun ID n'est trouvé, retournez 1 comme prochain ID
    }
    public void assignSymptomToFiche(Long ficheId, Symptom symptom) {
        Fiche fiche = ficheRepository.findById(ficheId).orElseThrow(() -> new EntityNotFoundException("Fiche not found"));
        symptom.setMyFiche(fiche);
        fiche.getSymptoms().add(symptom);
        ficheRepository.save(fiche);
    }
    public Fiche addFicheAndAssignFicheToUserAndDoctor(Fiche fiche, int userId1, int userId2) {
        // Retrieve the user and doctor with the provided IDs
        OurUsers user1 = ourUserRepo.findById(userId1).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        OurUsers user2 = ourUserRepo.findById(userId2).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Check if one is a user and the other is a doctor
        if (user1.getRole().equals("USER") && user2.getRole().equals("DOCTOR")) {
            fiche.setEtudiant(user1);
            fiche.setPsy(user2);
            System.out.println(user1.getId());
            System.out.println(user2.getId());
        } else if (user1.getRole().equals("DOCTOR") && user2.getRole().equals("USER")) {
            fiche.setEtudiant(user2);
            fiche.setPsy(user1);
            System.out.println(user1.getId());
            System.out.println(user2.getId());
        } else {
            throw new IllegalArgumentException("Un utilisateur doit être un patient et l'autre un docteur");
        }

        // Save and return the fiche
        return ficheRepository.save(fiche);
    }


    public Fiche DesaffecterPatientFromFiche(Long ficheId) {
        Fiche fiche = ficheRepository.findById(ficheId)
                .orElseThrow(() -> new RuntimeException("Fiche introuvable"));

        // Save the user_id before unassigning
        int userId = fiche.getEtudiant().getId();

        // Dissociate the patient from the fiche by setting to null
        fiche.setEtudiant(null);

        // Save the changes to the fiche
        Fiche updatedFiche = ficheRepository.save(fiche);

        // Now you can save the user_id if needed
        // userService.saveUserId(userId);

        return updatedFiche;
    }

    public Fiche DesaffecterDoctorFromFiche(Long ficheId) {
        Fiche fiche = ficheRepository.findById(ficheId)
                .orElseThrow(() -> new RuntimeException("Fiche introuvable"));

        // Save the doctor_id before unassigning
        int doctorId = fiche.getPsy().getId();

        // Dissociate the doctor from the fiche by setting to null
        fiche.setPsy(null);

        // Save the changes to the fiche
        Fiche updatedFiche = ficheRepository.save(fiche);

        // Now you can save the doctor_id if needed
        // doctorService.saveDoctorId(doctorId);

        return updatedFiche;
    }


    public Fiche getFicheByUser(int userId) {
        return ficheRepository.findByEtudiantId(userId);
    }


    public int findEtudiantIdByFicheId(Long ficheId) {
        return ficheRepository.findEtudiantIdByFicheId(ficheId);
    }



}
