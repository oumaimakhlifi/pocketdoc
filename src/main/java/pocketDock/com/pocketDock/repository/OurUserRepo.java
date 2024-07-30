package pocketDock.com.pocketDock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pocketDock.com.pocketDock.entity.OurUsers;

import java.util.List;
import java.util.Optional;


public interface OurUserRepo extends JpaRepository<OurUsers,Integer> {
    Optional<OurUsers> findByEmail(String email);
    public boolean existsByEmail(String email);
    OurUsers findByResetToken(String resetToken);

    @Query("SELECT u FROM OurUsers u WHERE u.role = 'USER' AND NOT EXISTS (SELECT f FROM Fiche f WHERE f.etudiant = u)")
    List<OurUsers> findUsersWithoutFiche();
    public Optional<OurUsers> findById(Integer id);
    @Query("SELECT u.email FROM OurUsers u WHERE u.role = 'Doctor'")
    List<String> findEmailsOfPsyUsers();
    @Query("SELECT u FROM OurUsers u WHERE u.id = :userId")
    OurUsers findUsersById(int userId);
    List<OurUsers> findByRole(String role);
}
