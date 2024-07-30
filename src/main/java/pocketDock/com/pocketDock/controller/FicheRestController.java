package pocketDock.com.pocketDock.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.FicheWithFullName;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.repository.FicheRepository;
import pocketDock.com.pocketDock.repository.OurUserRepo;
import pocketDock.com.pocketDock.repository.SymptomRepository;
import pocketDock.com.pocketDock.service.FicheServiceImpl;
import pocketDock.com.pocketDock.service.IFicheService;
import pocketDock.com.pocketDock.service.StatisticService;
import pocketDock.com.pocketDock.service.UserServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/fiche")
@AllArgsConstructor
public class FicheRestController {
    @Autowired
   FicheRepository ficheRepository;
    @Autowired
    IFicheService ficheService;
    @Autowired
    SymptomRepository symptomService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    OurUserRepo ourUserRepo;
    @Autowired
    FicheServiceImpl ficheServicei;
    @Autowired
    UserServiceImpl us;

    @GetMapping("/stat-by-type-addiction")
    public Map<String, Double> getFichePercentageByTypeAddiction() {
        Map<String, Long> ficheCountByType = statisticService.getFicheCountByTypeAddiction();
        long totalFicheCount = ficheCountByType.values().stream().mapToLong(Long::valueOf).sum();

        Map<String, Double> fichePercentageByType = new HashMap<>();
        ficheCountByType.forEach((type, count) -> {
            double percentage = (count.doubleValue() / totalFicheCount) * 100;
            fichePercentageByType.put(type, percentage);
        });

        return fichePercentageByType;
    }







    @PostMapping("/add-fiche/{userId}/{doctorId}")
    public Fiche addFiche(@RequestBody Fiche fiche, @PathVariable("userId") int userId, @PathVariable("doctorId") int doctorId) {
        // Find the user and doctor
        OurUsers user = ourUserRepo.findUsersById(userId);
        OurUsers doctor = ourUserRepo.findUsersById(doctorId);
        // Check if one is a user and the other is a doctor


        // Save and return the fiche
        return ficheService.addFicheAndAssignFicheToUserAndDoctor(fiche, userId, doctorId);
    }

    @GetMapping("/retrieve-fiches-with-usernames/{userId}")
    public List<FicheWithFullName> retrieveFichesAndUsernamesByUserId(@PathVariable int userId) {
        return ficheRepository.retrieveFichesAndUsernamesByUserId(userId);
    }









    @GetMapping("/retrieve-fiches/{user-id}")
    public Map<String, Object> retrieveAllFiches(@PathVariable("user-id") int userId) {
        // Récupérer l'utilisateur
        OurUsers user = ourUserRepo.findUsersById(userId);

        // Vérifier si l'utilisateur existe
        if (user == null) {
            // Gérer le cas où l'utilisateur n'existe pas
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Utilisateur introuvable avec l'ID : " + userId);
            return response;
        }

        // Déterminer le rôle de l'utilisateur
        String role = user.getRole();
        List<Fiche> allFiches = new ArrayList<>();
        int pageNumber = 0;
        boolean hasNextPage = true;

        // Récupérer les fiches correspondantes en fonction du rôle de l'utilisateur
        if ("DOCTOR".equals(role)) {
            do {
                // Utiliser la méthode findByPsy avec pagination par lot de 5
                Page<Fiche> fichePage = ficheRepository.findByPsy(user, PageRequest.of(pageNumber, 5));
                List<Fiche> fiches = fichePage.getContent();
                allFiches.addAll(fiches);
                pageNumber++;
                hasNextPage = fichePage.hasNext();
            } while (hasNextPage);

            // Si le nombre total de fiches récupérées est inférieur à 5, récupérez les fiches restantes sans pagination
            if (allFiches.size() % 5 != 0) {
                Page<Fiche> remainingFichesPage = ficheRepository.findByPsy(user, PageRequest.of(pageNumber, allFiches.size() % 5));
                allFiches.addAll(remainingFichesPage.getContent());
            }
        }

        // Créer la réponse
        Map<String, Object> response = new HashMap<>();
        if (!allFiches.isEmpty()) {
            List<Map<String, Object>> fichesData = new ArrayList<>();
            for (Fiche fiche : allFiches) {
                Map<String, Object> ficheData = new HashMap<>();
                ficheData.put("idFiche", fiche.getIdFiche());
                ficheData.put("historiq_fam", fiche.getHistoriq_fam());
                ficheData.put("eval_psy", fiche.getEval_psy());
                ficheData.put("notes_de_suivi", fiche.getNotes_de_suivi());

                // Ajoutez d'autres informations sur la fiche au besoin

                fichesData.add(ficheData);
            }
            response.put("fiches", fichesData);
        } else {
            response.put("message", "Aucune fiche trouvée pour l'utilisateur avec l'ID : " + userId);
        }

        return response;
    }

    @GetMapping("/total-fiches/{userId}")
    public int getTotalFichesForUser(@PathVariable("userId") int userId) {
        return ficheRepository.countByPsyId(userId);
    }

    @DeleteMapping("/remove-fiche/{fiche-id}")
    public void removeFiche(@PathVariable("fiche-id") Long ficheId) {
        ficheService.removeFiche(ficheId);
    }



    @PutMapping("/modify-fiche/{ficheId}")
    public Fiche modifyFiche(@RequestBody Fiche fiche, @PathVariable("ficheId") Long ficheId) {
        // Votre logique de traitement ici

    // Vérifier si la fiche existe dans la base de données en utilisant l'ID passé en paramètre
        Fiche existingFiche = ficheRepository.findByIdFiche(ficheId);
        if (existingFiche != null) {
            // Mettre à jour les champs de la fiche existante avec les valeurs de la fiche passée en paramètre
            existingFiche.setHistoriq_fam(fiche.getHistoriq_fam());
            existingFiche.setEval_psy(fiche.getEval_psy());
            existingFiche.setNotes_de_suivi(fiche.getNotes_de_suivi());

            // Enregistrer les modifications dans la base de données
            return ficheRepository.save(existingFiche);
        } else {
            // Si la fiche n'est pas trouvée, lancer une exception
            throw new NoSuchElementException("La fiche avec l'ID " + ficheId + " n'existe pas.");
        }
    }


    @GetMapping("/next-fiche-id")
    public Long getNextFicheId() {
        Long nextId = ficheService.getNextFicheId();
        return nextId;
    }
    @GetMapping("/without-fiche")
    public List<OurUsers> findUsersWithoutFiche() {
        return us.findUsersWithoutFiche();
    }


    @GetMapping("/{ficheId}")
    public Integer getEtudiantIdByFicheId(@PathVariable Long ficheId) {
        return ficheService.findEtudiantIdByFicheId(ficheId);
    }




    @GetMapping("/get-fiche/{ficheId}")
    public ResponseEntity<Fiche> getFicheById(@PathVariable Long ficheId) {
        Optional<Fiche> ficheOptional = ficheRepository.findById(ficheId);
        if (ficheOptional.isPresent()) {
            Fiche fiche = ficheOptional.get();
            return ResponseEntity.ok(fiche);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
















}
