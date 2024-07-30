package pocketDock.com.pocketDock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pocketDock.com.pocketDock.dto.ReqRes;
import pocketDock.com.pocketDock.entity.Convention;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.entity.Product;
import pocketDock.com.pocketDock.repository.ProductRepo;
import pocketDock.com.pocketDock.service.IConventionService;
import pocketDock.com.pocketDock.service.UserService;

import java.util.List;

@RestController
public class AdminUsers {
    @Autowired
    private ProductRepo productRepo ;
    @Autowired
    private  UserService userService;
    @Autowired
    private IConventionService conventionService;
    @GetMapping("/public/product")
    public ResponseEntity<Object> getAllProducts(){
        return ResponseEntity.ok(productRepo.findAll());
    }
    @PostMapping("/admin/saveproduct")
    public ResponseEntity<Object> signUp(@RequestBody ReqRes productRequest){
        Product productToSave =new Product();
        productToSave.setName(productRequest.getName());
        return ResponseEntity.ok(productRepo.save(productToSave));
    }
    @GetMapping("/user/alone")
    public ResponseEntity<Object> userAlone(){
        return ResponseEntity.ok("Users alone can acces this api only");
    }
    @GetMapping("/adminuser/both")
    public ResponseEntity<Object> bothAdminAndUserApi(){
        return ResponseEntity.ok("both admin and Users  can acces this api only");
    }
    @PutMapping("/assign-convention/{user-id}/{convention-id}")
    public void assignConventionToUser(@PathVariable("user-id") int userId, @PathVariable("convention-id") Long conventionId) {
        // Récupérer l'utilisateur et la convention
        OurUsers user = userService.retrieveUser(userId);
        Convention convention = conventionService.retrieveConvention(conventionId);

        // Vérifier si l'utilisateur est un psychologue
        if (user != null && user.getRole() == "Doctor") {
            // Affecter la convention uniquement aux psychologues
            user.setConvention(convention);
            userService.modifyUser(user); // Sauvegarder les modifications
        } else {
            // Gérer le cas où l'utilisateur n'est pas un psychologue
            // Par exemple, retourner un message d'erreur ou lancer une exception
            throw new IllegalArgumentException("Only psychologists can be assigned conventions.");
        }
    }

}


