package pocketDock.com.pocketDock.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.entity.Symptom;
import pocketDock.com.pocketDock.repository.FicheRepository;
import pocketDock.com.pocketDock.repository.OurUserRepo;
import pocketDock.com.pocketDock.repository.SymptomRepository;
import pocketDock.com.pocketDock.service.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/symptom")
@AllArgsConstructor
public class SymptomRestController {
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private final ISymptomService symptomService;
    @Autowired
    FicheRepository ficheRepository;
    @Autowired
    SymptomRepository symptomRepository;
    @Autowired
    OurUserRepo userRepository;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private FicheServiceImpl ficheService;

    @GetMapping("/stat-by-intensity")
    public Map<String, Double> getSymptomPercentageByIntensity() {
        Map<String, Long> symptomCountsByIntensity = statisticService.getSymptomCountsByIntensity();
        long totalSymptomCount = symptomCountsByIntensity.values().stream().mapToLong(Long::valueOf).sum();

        Map<String, Double> symptomPercentageByIntensity = new HashMap<>();
        symptomCountsByIntensity.forEach((intensity, count) -> {
            double percentage = (count.doubleValue() / totalSymptomCount) * 100;
            symptomPercentageByIntensity.put(intensity, percentage);
        });

        return symptomPercentageByIntensity;
    }
    @GetMapping("/retrieve-all-symptoms")
    public List<Map<String, Object>> getSymptomsWithUser() {
        List<Symptom> symptoms = symptomService.getSymptoms(); // Récupérer tous les symptômes
        List<Map<String, Object>> symptomsWithUser = new ArrayList<>();

        for (Symptom symptom : symptoms) {
            Map<String, Object> symptomData = new HashMap<>();
            symptomData.put("idSymptom", symptom.getId());
            symptomData.put("date_deb", symptom.getDate());
            symptomData.put("description", symptom.getDescription());
            symptomData.put("duration", symptom.getDuration());
            symptomData.put("emplacement_douleur", symptom.getLocation());
            symptomData.put("Severity", symptom.getSeverity());
            symptomData.put("declencheurs", symptom.getTriggers());
            symptomData.put("Type", symptom.getType());
            symptomData.put("fiche_corresp", symptom.getMyFiche());
            // Données de l'utilisateur
            OurUsers user = symptom.getMyFiche().getEtudiant();
            if (user != null) {
                symptomData.put("userName", user.getName());
                symptomData.put("userLastname", user.getLastname());
                symptomData.put("userCity", user.getCity());
                symptomData.put("userCountry", user.getCountry());
                symptomData.put("userRue", user.getRue());
                symptomData.put("userDatenaissance", user.getDatenaissance());
                symptomData.put("userTelephone", user.getTelephone());
                symptomData.put("userRole", user.getRole());
                symptomData.put("userProfileImageUrl", user.getProfileImageUrl());
            }

            symptomsWithUser.add(symptomData);
        }

        return symptomsWithUser;
    }


    @PostMapping("/add-symptom/{user-id}")
    public ResponseEntity<String> addSymptomAndAssignFicheToSymptom(@RequestBody Symptom symptom, @PathVariable ("user-id")int userid) {
        System.out.println(userid);
        OurUsers user = userRepository.findUsersById(userid);
        Fiche fiche =ficheService.getLatestFicheByEtudiant(user);
        System.out.println(user);
        System.out.println(fiche.getIdFiche());
        if (fiche != null&& symptom!=null) {

            long ficheid=fiche.getIdFiche();
            symptom.setDate(LocalDateTime.now());
            Symptom addedSymptom = symptomService.addSymptomAndAssignFicheToSymptom(symptom,ficheid,userid);

            if (addedSymptom != null) {

                // Appel à la méthode d'envoi d'e-mail ici
                emailSenderService.sendHighSeveritySymptomEmail(addedSymptom,fiche);
                return ResponseEntity.ok("Symptom added successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add symptom");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT AVAILABLE");
        }
    }
    @GetMapping("/retrieve-symptom/{user-id}")
    public List<List<Object>> findSymptomByUserId(@PathVariable("user-id") int userId) {
        // Récupérer le psychologue à partir de son ID
        OurUsers doctor = userRepository.findUsersById(userId);

        // Vérifier si le psychologue existe
        if (doctor == null) {
            // Retourner une liste vide si le psychologue n'existe pas
            return new ArrayList<>();
        }

        // Récupérer toutes les fiches associées à ce psychologue
        List<Fiche> fiches = ficheRepository.findByPsy2(userId);

        // Initialiser une liste pour stocker tous les symptômes avec les noms d'utilisateur correspondants
        List<List<Object>> allSymptoms = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        // Pour chaque fiche, récupérer les symptômes associés et les ajouter à la liste principale avec le nom d'utilisateur correspondant
        for (Fiche fiche : fiches) {
            Set<Symptom> symptoms = fiche.getSymptoms();
            String username = fiche.getEtudiant().getName() + " " + fiche.getEtudiant().getLastname();

            // Pour chaque symptôme, ajouter le symptôme et le nom d'utilisateur à une sous-liste
            for (Symptom symptom : symptoms) {
                LocalDateTime symptomTime = symptom.getDate();
                if (symptomTime != null && currentTime.minusHours(48).isBefore(symptomTime)) {
                    List<Object> userSymptoms = new ArrayList<>();
                    userSymptoms.add(symptom);// Ajouter le symptôme à la sous-liste
                    userSymptoms.add(username); // Ajouter le nom d'utilisateur à la sous-liste
                    allSymptoms.add(userSymptoms); // Ajouter la sous-liste à la liste principale
                }
            }
        }
        return allSymptoms;
    }



    @GetMapping("/retrieve-symptom1/{symptom-id}")
    public Map<String, Object> retrieveSymptomWithUser(@PathVariable("symptom-id") Long symptomId) {
        Symptom symptom = symptomService.retrieveSymptom(symptomId);
        Map<String, Object> symptomData = new HashMap<>();

        if (symptom != null) {
            symptomData.put("idSymptom", symptom.getId());
            symptomData.put("date_deb", symptom.getDate());
            symptomData.put("description", symptom.getDescription());
            symptomData.put("duration", symptom.getDuration());
            symptomData.put("emplacement_douleur", symptom.getLocation());
            symptomData.put("Severity", symptom.getSeverity());
            symptomData.put("declencheurs", symptom.getTriggers());
            symptomData.put("Type", symptom.getType());
            symptomData.put("fiche_corresp", symptom.getMyFiche());

            // Données de l'utilisateur
            OurUsers user = symptom.getMyFiche().getEtudiant();
            if (user != null) {
                symptomData.put("userName", user.getName());
                symptomData.put("userLastname", user.getLastname());
                symptomData.put("userCity", user.getCity());
                symptomData.put("userCountry", user.getCountry());
                symptomData.put("userRue", user.getRue());
                symptomData.put("userDatenaissance", user.getDatenaissance());
                symptomData.put("userTelephone", user.getTelephone());
                symptomData.put("userRole", user.getRole());
                symptomData.put("userProfileImageUrl", user.getProfileImageUrl());
            }
        }

        return symptomData;
    }


    @DeleteMapping("/remove-symptom/{symptom-id}")
    public void removeSymptom(@PathVariable("symptom-id") Long symptomId) {
        symptomService.DesaffecterSymptomFromFiche(symptomId);
        symptomService.removeSymptom(symptomId);
    }

    @PutMapping("/modify-symptom/{symptom-id}")
    public Symptom modifySymptom(@RequestBody Symptom symptom) {

        return symptomService.modifySymptom(symptom);
    }



    @GetMapping("/count-unseen-symptoms/{ficheId}")
    public int countUnseenSymptomsForFiche(@PathVariable Long ficheId) {

        return symptomService.countUnseenSymptomsForFiche(ficheId);
    }



}
