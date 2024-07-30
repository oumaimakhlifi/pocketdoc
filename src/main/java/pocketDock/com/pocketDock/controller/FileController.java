package pocketDock.com.pocketDock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pocketDock.com.pocketDock.entity.FileEntity;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.repository.FileRepository;
import pocketDock.com.pocketDock.repository.OurUserRepo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/dem")
public class FileController {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    OurUserRepo userRepo;
    @PostMapping("/upload/{docId}")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,@PathVariable("docId") int docId) {
        try {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setData(file.getBytes());
            fileEntity.setDoc(userRepo.findUsersById(docId));
            fileRepository.save(fileEntity);
            String message = "File uploaded successfully!";
            HttpStatus httpStatus = HttpStatus.CREATED;
            // Retourner 100 lorsque le téléchargement est terminé
            return ResponseEntity.status(httpStatus).body(100);
        } catch (IOException e) {
            String errorMessage = "Failed to upload file: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @GetMapping("/files/{user-id}")
    public ResponseEntity<List<FileEntity>> getUserFiles(@PathVariable("user-id") int userId) {
        // Rechercher les fichiers de l'utilisateur en fonction de son ID
        List<FileEntity> userFiles = fileRepository.findByDocId(userId);

        // Vérifier si des fichiers ont été trouvés pour cet utilisateur
        if (userFiles.isEmpty()) {
            return ResponseEntity.notFound().build(); // Retourner une réponse 404 si aucun fichier n'est trouvé
        }

        // Retourner tous les fichiers avec une réponse 200 OK
        return ResponseEntity.ok(userFiles);
    }


    @GetMapping("/files-with-user-ids")
    public ResponseEntity<List<Object[]>> getFilesWithUserIds() {
        List<Object[]> filesWithUserIds = fileRepository.findAll().stream()
                .map(file -> new Object[]{file, file.getDoc().getId()})
                .collect(Collectors.toList());
        return ResponseEntity.ok(filesWithUserIds);
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        Optional<FileEntity> optionalFileEntity = fileRepository.findById(id);
        if (optionalFileEntity.isPresent()) {
            FileEntity fileEntity = optionalFileEntity.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(fileEntity.getContentType()));
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileEntity.getFilename()).build());
            ByteArrayResource resource = new ByteArrayResource(fileEntity.getData());
            return ResponseEntity.ok().headers(headers).body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
            Optional<FileEntity> optionalFileEntity = fileRepository.findById(id);
            if (optionalFileEntity.isPresent()) {
                fileRepository.deleteById(id);
                String message = "File deleted successfully!";
                // Récupérer la liste mise à jour des fichiers après la suppression
                List<FileEntity> updatedFiles = fileRepository.findAll();
                return ResponseEntity.ok(updatedFiles);
            } else {
                String message = "File not found!";
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/count/{userId}")
    public ResponseEntity<String> checkFileCountByUser(@PathVariable int userId) {
        // Recherche de l'utilisateur dans la base de données
        List<FileEntity> files = fileRepository.findByDocId(userId);
        OurUsers user = userRepo.findUsersById(userId);

        // Vérifier si l'utilisateur existe
        if (user == null) {
            return ResponseEntity.notFound().build(); // Retourner une réponse 404 si l'utilisateur n'est pas trouvé
        }

        // Obtenir le nombre de fichiers pour l'utilisateur
        int fileCount = files.size();

        // Retourner le nombre de fichiers avec une réponse 200 OK
        if (fileCount == 0) {
            return ResponseEntity.ok("No files found for the user");
        } else if (fileCount == 1) {
            return ResponseEntity.ok("File count is 1");
        } else {
            return ResponseEntity.ok("More than 1 file found for the user");
        }
    }

    @GetMapping("/findByUser/{userId}")
    public ResponseEntity<List<FileEntity>> findFilesByUser(@PathVariable Integer userId) {
        // Recherche de l'utilisateur dans la base de données
        OurUsers user = userRepo.findUsersById(userId);

        // Vérification si l'utilisateur existe
        if (user == null) {
            return ResponseEntity.notFound().build(); // Retourner une réponse 404 si l'utilisateur n'est pas trouvé
        }

        // Récupération des fichiers associés à l'utilisateur
        List<FileEntity> files = fileRepository.findByDocId(userId);

        // Retourner les fichiers avec une réponse 200 OK
        return ResponseEntity.ok(files);
    }

}



