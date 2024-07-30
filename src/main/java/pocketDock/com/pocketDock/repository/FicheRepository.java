package pocketDock.com.pocketDock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.FicheWithFullName;
import pocketDock.com.pocketDock.entity.OurUsers;


import java.util.List;

@Repository
public interface FicheRepository extends JpaRepository<Fiche,Long> {
    @Query("SELECT MAX(f.idFiche) FROM Fiche f")
    Long findMaxFicheId();
    Fiche findByIdFiche(long id);

    @Query("SELECT f.etudiant FROM Fiche f WHERE f.idFiche = :ficheId")
    OurUsers findEtudiantByFicheId(Long ficheId);
    @Query("SELECT COUNT(f) FROM Fiche f WHERE f.type_addic = 'ALCOOL'")
    public Long countFichesAlcool();

    @Query("SELECT COUNT(f) FROM Fiche f WHERE f.type_addic = 'TABAC'")
    public Long countFichesTabac();

    @Query("SELECT COUNT(f) FROM Fiche f WHERE f.type_addic = 'DROGUE'")
    public Long countFichesDrogue();
    Page<Fiche> findByPsy(OurUsers user, Pageable pageable);
    List<Fiche> findByEtudiant(OurUsers etudiant);
    Fiche findByEtudiantId(int userId);
Fiche getFicheByIdFiche(long id);
    @Query("SELECT f.etudiant.id FROM Fiche f WHERE f.idFiche = :ficheId")
    int findEtudiantIdByFicheId(Long ficheId);

    @Query("SELECT f FROM Fiche f WHERE f.etudiant = :etudiant ORDER BY f.idFiche DESC")
    List<Fiche> getLatestFicheByEtudiant(@Param("etudiant") OurUsers etudiant);




    @Query("SELECT f FROM Fiche f WHERE f.etudiant.id = :etuId")
List<Fiche>afficheAllFcihesFor1user(@Param("etuId") int etuId);






    @Query("SELECT COUNT(f) FROM Fiche f WHERE f.psy.id = :userId")
    int countByPsyId(@Param("userId") int userId);

    @Query("SELECT f AS fiche, CONCAT(u.name, ' ', u.lastname) AS fullName FROM Fiche f JOIN f.etudiant u WHERE f.psy.id = :userId")
    List<FicheWithFullName> retrieveFichesAndUsernamesByUserId(@Param("userId") int userId);


Fiche findByPsy(OurUsers doc);


    @Query("SELECT f AS fiche From Fiche f JOIN f.etudiant u WHERE f.psy.id = :docid")
List<Fiche>findByPsy2(@Param("docid") int docid);
}



