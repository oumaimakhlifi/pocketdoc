package pocketDock.com.pocketDock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.entity.Symptom;
import pocketDock.com.pocketDock.repository.FicheRepository;
import pocketDock.com.pocketDock.repository.OurUserRepo;
import pocketDock.com.pocketDock.repository.SymptomRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SymptomServiceImpl implements ISymptomService{
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    SymptomRepository symptomRepository;
    @Autowired
    FicheRepository ficheRepository; // Injection de FicheRepository
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private OurUserRepo userRepository;
    @Override
    public List<Symptom> getSymptoms() {
        return symptomRepository.findAll();
    }

    @Override
    public Symptom retrieveSymptom(Long symptomId) {
        Optional<Symptom> symptomOptional = symptomRepository.findById(symptomId);
        return symptomOptional.orElse(null);
    }

    public Symptom addSymptomAndAssignFicheToSymptom(Symptom symptom, Long ficheid, int userid) {
        Fiche fiche = ficheRepository.findByIdFiche(ficheid);
        Optional<OurUsers> etudiantOptional = userRepository.findById(userid);

        if (fiche!=null && etudiantOptional.isPresent()) {
            OurUsers etudiant = etudiantOptional.get();

            // on set le fils dans le parent :
            symptom.setMyFiche(fiche);

           // System.out.println(symptom.getMyFiche().getPsy().toString());
            return symptomRepository.save(symptom);
        } else {
            throw new NoSuchElementException("Fiche with ID " + ficheid + " or User with ID " + userid + " not found");
        }
    }
    @Override
    public Fiche DesaffecterSymptomFromFiche(Long sympId) {
        // Récupérer le symptôme associé à l'ID fourni
        Symptom symptom = symptomRepository.findSymptomById(sympId);

        // Vérifier si le symptôme est trouvé
        if (symptom == null) {
            throw new RuntimeException("Symptôme introuvable avec l'ID : " + sympId);
        }

        // Récupérer la fiche associée au symptôme
        Fiche fiche = symptom.getMyFiche();

        // Vérifier si la fiche est trouvée
        if (fiche == null) {
            throw new RuntimeException("Fiche introuvable pour le symptôme avec l'ID : " + sympId);
        }

        symptom.setMyFiche(null);
        // Supposons que vous ayez une méthode dans votre SymptomRepository pour supprimer le symptôme
        symptomRepository.delete(symptom);

        // Retourner la fiche mise à jour
        return fiche;
    }
    @Override
    public void removeSymptom(Long symptomId) {
        symptomRepository.deleteById(symptomId);
    }

    @Override
    public Symptom modifySymptom(Symptom symptom) {
        return symptomRepository.save(symptom);
    }

    public int countUnseenSymptomsForFiche(Long ficheId) {
        return symptomRepository.countUnseenSymptomsForFiche(ficheId);
    }

    public List<Symptom> findSymptomsByMyFiche(Fiche fiche) {
        return symptomRepository.findSymptomByMyFiche(fiche);
    }

}