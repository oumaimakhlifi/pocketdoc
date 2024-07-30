package pocketDock.com.pocketDock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pocketDock.com.pocketDock.entity.FileEntity;
import pocketDock.com.pocketDock.entity.OurUsers;

import java.io.File;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity,Long> {
    @Query("SELECT COUNT(f) FROM FileEntity f WHERE f.doc = :user")
    int countFilesByUser(OurUsers user);
    @Query("SELECT f FROM FileEntity f WHERE f.doc.id = :userId")
    List<FileEntity> findByDocId(int userId);

}
