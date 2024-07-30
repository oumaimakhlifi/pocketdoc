package pocketDock.com.pocketDock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.Symptom;

import java.util.List;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom,Long> {
    @Query("SELECT COUNT(s) FROM Symptom s WHERE s.myFiche.idFiche = :ficheId AND s.seenByDoctor = 0")
    int countUnseenSymptomsForFiche(Long ficheId);
    @Query("SELECT COUNT(s) FROM Symptom s WHERE s.severity = 'HIGH'")
    Long countSymptomsHighIntensity();

    @Query("SELECT COUNT(s) FROM Symptom s WHERE s.severity = 'MODERATE'")
    Long countSymptomsModerateIntensity();

    @Query("SELECT COUNT(s) FROM Symptom s WHERE s.severity = 'WEAK'")
    Long countSymptomsWeakIntensity();


    Symptom findSymptomById(Long id);

   List<Symptom> findSymptomByMyFiche(Fiche f);







}
