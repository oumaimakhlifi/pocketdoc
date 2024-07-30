package pocketDock.com.pocketDock.controller;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pocketDock.com.pocketDock.entity.Convention;
import pocketDock.com.pocketDock.entity.FileEntity;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.repository.ConventionRepository;
import pocketDock.com.pocketDock.repository.FileRepository;
import pocketDock.com.pocketDock.repository.OurUserRepo;
import pocketDock.com.pocketDock.service.ConventionCloseToExpire;
import pocketDock.com.pocketDock.service.ExportConventionService;
import pocketDock.com.pocketDock.service.IConventionService;
import pocketDock.com.pocketDock.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/convention")
@AllArgsConstructor
public class ConventionRestController {
    ConventionRepository conventionRepository;
    IConventionService conventionService;
    UserService userService;
    OurUserRepo userRepository;
    FileRepository fileRepository;
    @Autowired
    private ExportConventionService exportService;
@Autowired
    ConventionCloseToExpire ce;



    @GetMapping("/renew")
    public void renewConvention() {
        boolean success = ce.renewConvention();
        if (success) {
            System.out.println("Renewal successful");
            System.out.println("La convention a été renouvelée avec succès.");
        } else {
            System.out.println("Renewal failed");
            System.out.println("Impossible de renouveler la convention pour cet utilisateur.");
        }
    }

















    @GetMapping("/retrieve-all-conventions")
    public List<Map<String, Object>> getConventions() {
        List<Convention> listConventions = conventionService.getConventions(); // Récupérer toutes les conventions
        List<Map<String, Object>> conventionsWithData = new ArrayList<>();

        for (Convention convention : listConventions) {
            Map<String, Object> conventionData = new HashMap<>();
            conventionData.put("id", convention.getIdConv());
            conventionData.put("description", convention.getDescription());
            conventionData.put("date_deb", convention.getDateDebut());
            conventionData.put("date_fin", convention.getDateFin());
            conventionData.put("condition_resi", convention.getConditionsResiliation());
            conventionData.put("services_inclus", convention.getServicesInclus());
            conventionData.put("pourcentage_reduc", convention.getReductionFraisConsultation());
            conventionData.put("cin", convention.getCin());
            // Données de  med  associé
            OurUsers med = convention.getMed();
            if (med != null) {
                conventionData.put("studentName", med.getName());
                conventionData.put("studentLastname", med.getLastname());
                conventionData.put("studentDatenaissance", med.getDatenaissance());
                conventionData.put("studentProfileImageUrl", med.getProfileImageUrl());
            }

            conventionsWithData.add(conventionData);
        }

        return conventionsWithData;
    }


    @PostMapping("/add-convention/{doctor-id}")
    public Convention addConvention(@RequestBody Convention convention, @PathVariable("doctor-id") int doctorId) {
        // Utiliser addConventionAndAssignConventionToDoctor pour ajouter et associer la convention au docteur
        return conventionService.addConventionAndAssignConventionToDoctor(convention, doctorId);
    }
    @GetMapping("/testConvention/{doctor-id}")
    public boolean isConventionExpired(@PathVariable("doctor-id") int userId) {
        OurUsers doctor = userRepository.findUsersById(userId);
        Convention convention = conventionRepository.findConventionByUserId(userId);
        Date currentDate = new Date();

        // Ensure convention and end date are not null
        if (convention != null && convention.getDateFin() != null) {
            Date endDate = new Date(convention.getDateFin().getTime());

            // Check if end date of convention is after the current date
            if (endDate.after(currentDate)) {

                return false; // Convention is not expired
            } else {

                return true;
                // Convention is expired
            }
        } else {
            return false; // Handle null convention or end date
        }
    }


    @GetMapping("/retrieve-convention/{user-id}")
    public Map<String, Object> retrieveConvention(@PathVariable("user-id") int userId) {
        OurUsers med = userRepository.findUsersById(userId);
        Convention convention=conventionRepository.findConventionByUserId(userId);
        Map<String, Object> conventionData = new HashMap<>();
        conventionData.put("id", convention.getIdConv());
        conventionData.put("description", convention.getDescription());
        conventionData.put("date_deb", convention.getDateDebut());
        conventionData.put("date_fin", convention.getDateFin());
        conventionData.put("condition_resi", convention.getConditionsResiliation());
        conventionData.put("services_inclus", convention.getServicesInclus());

        conventionData.put("cin", convention.getCin());

        // Données du médecin associé

        if (med != null) {
            conventionData.put("medName", med.getName());
            conventionData.put("medLastname", med.getLastname());
            conventionData.put("medDatenaissance", med.getDatenaissance());
            conventionData.put("medProfileImageUrl", med.getProfileImageUrl());
        }

        return conventionData;
    }


    @DeleteMapping("/remove-convention/{convention-id}")
    public void removeConvention(@PathVariable("convention-id") Long conventionId) {
        conventionService.DesaffecterMedecinFromConvention(conventionId);
        conventionService.removeConvention(conventionId);
    }

    @PutMapping("modify-convention/{conventionId}")
    public ResponseEntity<Convention> modifyConvention(@PathVariable long conventionId, @RequestBody Convention updatedConventionData) {
        try {
            Convention modifiedConvention = conventionService.modifyConvention(conventionId, updatedConventionData);
            return ResponseEntity.ok(modifiedConvention);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/export/pdf/{userId}")
    public ResponseEntity<InputStreamResource> exportTermsPdf(@PathVariable int userId) {

        Optional<OurUsers> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            OurUsers user = userOptional.get();
            String userName = user.getName()+" "+user.getLastname();

            Double userReduction = conventionRepository.findReductionFraisConsultationByMedId(userId);
            Convention convention = conventionRepository.findConventionByUserId(userId);


            String userCin = convention.getCin();
            System.out.println(userCin);
            List<Convention> conventions = Collections.singletonList(convention);
            String CR = convention.getConditionsResiliation();
            Date db = convention.getDateDebut();
            Date df =convention.getDateFin();
            return ExportConventionService.exportConventionPDF(conventions, userName, userCin, userReduction,CR,db,df);
        } else {
            // L'utilisateur n'a pas été trouvé, renvoyer une réponse appropriée
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportTermsExcel() {
        List<OurUsers> doctors = userRepository.findByRole("DOCTOR");

        if (!doctors.isEmpty()) {
            try (Workbook workbook = new XSSFWorkbook();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                Sheet sheet = workbook.createSheet("Conventions");

                // Création des titres des colonnes
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("PSY Name");
                headerRow.createCell(1).setCellValue("Reduction");
                headerRow.createCell(2).setCellValue("Montant après réduction");

                // Remplissage des données des conventions
                int rowNum = 1; // Commencer à partir de la deuxième ligne après les titres
                double totalMontant = 0.0;
                for (OurUsers user : doctors) {
                    String userName = user.getName();
                    Double userReduction = conventionRepository.findReductionFraisConsultationByMedId(user.getId());
                    Convention convention = conventionRepository.findConventionByUserId(user.getId());
                    double montantApresReduction;
                    if (userReduction != null) {
                        montantApresReduction = 120 - (120 * userReduction);
                    } else {
                        montantApresReduction = 120; // Utilisez 120 comme valeur par défaut
                    }
                    totalMontant += montantApresReduction;
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(userName);
                    row.createCell(1).setCellValue((userReduction != null) ? userReduction : 0.0); // Réduction nulle si elle est null
                    row.createCell(2).setCellValue(montantApresReduction);
                }

                // Ajouter une ligne pour la somme totale
                Row totalRow = sheet.createRow(rowNum);
                totalRow.createCell(0).setCellValue("Total");
                totalRow.createCell(2).setCellValue(totalMontant);

                workbook.write(out);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=conventions.xlsx");
                return ResponseEntity
                        .ok()
                        .headers(headers)
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(new InputStreamResource(new ByteArrayInputStream(out.toByteArray())));
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            // Aucun utilisateur trouvé avec le rôle DOCTOR, renvoyer une réponse 404 avec ResponseEntity.notFound().build()
            return ResponseEntity.notFound().build();
        }
    }


}
