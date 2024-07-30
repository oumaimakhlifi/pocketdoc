package pocketDock.com.pocketDock.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pocketDock.com.pocketDock.entity.Convention;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.repository.ConventionRepository;
import pocketDock.com.pocketDock.repository.OurUserRepo;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConventionServiceImpl implements IConventionService {
    @Autowired
    ConventionRepository conventionRepository;
    OurUserRepo ourUserRepo;

    @Override
    public List<Convention> getConventions() {
        return conventionRepository.findAll();

    }

    @Override
    public Convention retrieveConvention(Long conventionId) {
        return conventionRepository.findById(conventionId).orElse(null);

    }

    @Override
    public Convention addConvention(Convention convention) {
        return conventionRepository.save(convention);

    }

    public Convention addConventionAndAssignConventionToDoctor(Convention convention, int doctorId) {
        // Récupérer le docteur avec l'ID fourni
        OurUsers doctor = ourUserRepo.findById(doctorId).orElseThrow(() -> new RuntimeException("Docteur introuvable"));

        // Vérifier si l'utilisateur est bien un docteur


        // Assigner la convention au docteur
        convention.setMed(doctor);
        doctor.setConvention(convention);
        // Enregistrer et retourner la convention
        return conventionRepository.save(convention);
    }

    @Override
    public void removeConvention(Long conventionId) {
        conventionRepository.deleteById(conventionId);

    }

    public Convention modifyConvention(long conventionId, Convention updatedConventionData) {
        // Vérifier si la convention existe dans la base de données
        Optional<Convention> existingConventionOptional = conventionRepository.findById(conventionId);
        if (existingConventionOptional.isPresent()) {
            Convention existingConvention = existingConventionOptional.get();

            // Mettre à jour uniquement les champs modifiés
            if (Objects.nonNull(updatedConventionData.getDateDebut())) {
                existingConvention.setDateDebut(updatedConventionData.getDateDebut());
            }
            if (Objects.nonNull(updatedConventionData.getDateFin())) {
                existingConvention.setDateFin(updatedConventionData.getDateFin());
            }
            if (Objects.nonNull(updatedConventionData.getCin())) {
                existingConvention.setCin(updatedConventionData.getCin());
            }
            if (Objects.nonNull(updatedConventionData.getDescription())) {
                existingConvention.setDescription(updatedConventionData.getDescription());
            }
            if (Objects.nonNull(updatedConventionData.getReductionFraisConsultation())) {
                existingConvention.setReductionFraisConsultation(updatedConventionData.getReductionFraisConsultation());
            }
            if (Objects.nonNull(updatedConventionData.getServicesInclus())) {
                existingConvention.setServicesInclus(updatedConventionData.getServicesInclus());
            }
            if (Objects.nonNull(updatedConventionData.getConditionsResiliation())) {
                existingConvention.setConditionsResiliation(updatedConventionData.getConditionsResiliation());
            }

            // Enregistrer les modifications dans la base de données
            return conventionRepository.save(existingConvention);
        } else {
            // Si la convention n'est pas trouvée, lancer une exception
            throw new NoSuchElementException("La convention avec l'ID " + conventionId + " n'existe pas.");
        }
    }

    public Convention DesaffecterMedecinFromConvention(Long conventionId) {
        // Récupérer la convention avec l'ID fourni
        Convention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new RuntimeException("Convention introuvable"));

        // Sauvegarder l'ID du médecin avant de le désaffecter
        int doctorId = convention.getMed().getId();
        OurUsers doctor = ourUserRepo.findUsersById(doctorId);
        doctor.setConvention(null);
        // Dissocier le médecin de la convention en le définissant à null
        convention.setMed(null);

        // Enregistrer les modifications de la convention
        Convention updatedConvention = conventionRepository.save(convention);

        // Maintenant, vous pouvez enregistrer l'ID du médecin si nécessaire
        // userService.saveDoctorId(doctorId);

        return updatedConvention;
    }
    @Override
    public Convention modifyConvention2(long conventionId, Convention updatedConventionData) {
        // Vérifier si la convention existe dans la base de données
        Optional<Convention> existingConventionOptional = conventionRepository.findById(conventionId);
        if (existingConventionOptional.isPresent()) {
            Convention existingConvention = existingConventionOptional.get();

            // Mettre à jour les champs nécessaires
            existingConvention.setDateFin(updatedConventionData.getDateFin());

            // Enregistrer les modifications dans la base de données
            return conventionRepository.save(existingConvention);
        } else {
            // Si la convention n'est pas trouvée, lancer une exception
            throw new NoSuchElementException("La convention avec l'ID " + conventionId + " n'existe pas.");
        }
    }
}