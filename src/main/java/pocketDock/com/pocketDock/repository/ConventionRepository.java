package pocketDock.com.pocketDock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pocketDock.com.pocketDock.entity.Convention;

import java.util.List;

@Repository
public interface ConventionRepository extends JpaRepository<Convention,Long> {
    @Query("SELECT c FROM Convention c WHERE c.med.id = :userId")
    Convention findConventionByUserId(int userId);
    @Query("SELECT c.reductionFraisConsultation FROM Convention c WHERE c.med.id = :userId")
    Double findReductionFraisConsultationByMedId(int userId);
    List<Convention> findAll();
}
