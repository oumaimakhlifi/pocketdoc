package pocketDock.com.pocketDock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pocketDock.com.pocketDock.config.CloudinaryService;
import pocketDock.com.pocketDock.dto.ReqRes;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.repository.OurUserRepo;
import pocketDock.com.pocketDock.service.UserService;
import java.util.HashMap;
import java.util.UUID;
import java.util.Optional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private CloudinaryService cloudinaryService;


    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            String email = registrationRequest.getEmail();
            // Vérifier si l'email est déjà utilisé
            if (userService.checkEmail(email)) {
                resp.setStatusCode(600); // Bad request code
                resp.setMessage("Email already exists");
                return resp;
            }

            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(email);
            ourUsers.setName(registrationRequest.getName());
            ourUsers.setLastname(registrationRequest.getLastname()); // Ajouter le nom de famille
            ourUsers.setCity(registrationRequest.getCity()); // Ajouter la ville
            ourUsers.setCountry(registrationRequest.getCountry()); // Ajouter le pays
            ourUsers.setRue(registrationRequest.getRue()); // Ajouter la rue
            ourUsers.setCodePostal(registrationRequest.getCodePostal()); // Ajouter le code postal
            ourUsers.setDatenaissance(registrationRequest.getDatenaissance()); // Ajouter la date de naissance
            ourUsers.setAboutme(registrationRequest.getAboutme()); // Ajouter la description
            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUsers.setRole(registrationRequest.getRole());
            ourUsers.setTelephone(registrationRequest.getTelephone());

            if (registrationRequest.getRole().equals("USER")) {
                ourUsers.setStatus(1);
            } else {
                ourUsers.setStatus(0);
            }
            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
            if (ourUserResult != null && ourUserResult.getId() > 0) {
                resp.setOurUsers(ourUserResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }



    public ReqRes signIn(ReqRes signInRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
            var user = ourUserRepo.findByEmail(signInRequest.getEmail()).orElseThrow();
            System.out.println("USER IS :" + user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setId(user.getId());
            response.setMessage("Successfully Signed in");
            // Ajouter le rôle de l'utilisateur à la réponse
            response.setRole(user.getRole());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }



    public ReqRes refreshToken(ReqRes refreshTokenRegiest) {
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenRegiest.getToken());
        OurUsers users = ourUserRepo.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenRegiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenRegiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfuly Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }

    public ReqRes deleteUser(Integer userId) {
        ReqRes resp = new ReqRes();
        try {
            // Vérifier si l'utilisateur existe
            if (!ourUserRepo.existsById(userId)) {
                throw new Exception("User not found");
            }

            ourUserRepo.deleteById(userId);

            resp.setMessage("User Deleted Successfully");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    public ReqRes updateUser(Integer userId, ReqRes updateUserRequest) {
        ReqRes resp = new ReqRes();
        try {
            Optional<OurUsers> existingUserOptional = ourUserRepo.findById(userId);
            if (existingUserOptional.isPresent()) {
                OurUsers existingUser = existingUserOptional.get();

                existingUser.setName(updateUserRequest.getName());
                existingUser.setLastname(updateUserRequest.getLastname());
                existingUser.setCity(updateUserRequest.getCity());
                existingUser.setCountry(updateUserRequest.getCountry());
                existingUser.setRue(updateUserRequest.getRue());
                existingUser.setCodePostal(updateUserRequest.getCodePostal());
                existingUser.setDatenaissance(updateUserRequest.getDatenaissance());
                existingUser.setAboutme(updateUserRequest.getAboutme());

                OurUsers updatedUser = ourUserRepo.save(existingUser);
                resp.setOurUsers(updatedUser);
                resp.setMessage("User updated successfully");
                resp.setStatusCode(200);
            } else {
                resp.setStatusCode(404); // Not Found
                resp.setMessage("User not found");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes getUserById(Integer id) {
        ReqRes response = new ReqRes();
        try {
            Optional<OurUsers> userOptional = ourUserRepo.findById(id);
            OurUsers user = userOptional.orElseThrow();

            // Remplir les informations de l'utilisateur dans la réponse
            response.setOurUsers(user);

            // Générer le token JWT pour l'utilisateur
            String jwt = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            // Définir les autres propriétés de la réponse
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Retrieved User");
            response.setRole(user.getRole());
            response.setProfileImage(user.getProfileImageUrl());

            // Ajouter le reste des champs de l'utilisateur à la réponse
            response.setName(user.getName());
            response.setLastname(user.getLastname());
            response.setEmail(user.getEmail());
            response.setCity(user.getCity());
            response.setCountry(user.getCountry());
            response.setRue(user.getRue());
            response.setCodePostal(user.getCodePostal());
            response.setDatenaissance(user.getDatenaissance());
            response.setAboutme(user.getAboutme());
            response.setTelephone(user.getTelephone());

            // Ajoutez d'autres champs si nécessaire

        } catch (Exception e) {
            // En cas d'erreur, définir le code d'erreur et le message d'erreur dans la réponse
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }
    public void sendPasswordResetEmail(String userEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Reset Your Password");
        message.setText("To reset your password, click the link below:\n\n"
                + "http://yourapp.com/reset-password?token=" + resetToken);
        emailSender.send(message);
    }
    public void resetPassword(String token, String newPassword) {
        // 1. Vérifier la validité du token (vous devrez implémenter cette logique)

        // 2. Récupérer l'utilisateur associé au token
        OurUsers user = ourUserRepo.findByResetToken(token);
        if (user == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        // 3. Mettre à jour le mot de passe de l'utilisateur
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Supprimer le token de réinitialisation après utilisation
        ourUserRepo.save(user);
    }
    public void updateProfileImage(String email, String profileImageUrl) {
        Optional<OurUsers> userOptional = ourUserRepo.findByEmail(email);
        userOptional.ifPresent(user -> {
            user.setProfileImageUrl(profileImageUrl);
            ourUserRepo.save(user);
        });
    }
    public void updateProfileImage1(String email, String profileImageUrl1) {
        Optional<OurUsers> userOptional = ourUserRepo.findByEmail(email);
        userOptional.ifPresent(user -> {
            user.setProfileImageUrl1(profileImageUrl1);
            ourUserRepo.save(user);
        });
    }

}
