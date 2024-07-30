package pocketDock.com.pocketDock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pocketDock.com.pocketDock.dto.ReqRes;
import pocketDock.com.pocketDock.entity.Convention;
import pocketDock.com.pocketDock.entity.OurUsers;
import pocketDock.com.pocketDock.repository.OurUserRepo;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private OurUserRepo ourUserRepo;

    @Override
    public boolean checkEmail(String email) {
        return ourUserRepo.existsByEmail(email);
    }
    @Override
    public List<OurUsers> getAllUsers() {
        return ourUserRepo.findAll();
    }
    @Override
    public List<OurUsers> getUsers() {
        return ourUserRepo.findAll();
    }

    @Override
    public OurUsers retrieveUser(int userId) {
        Optional<OurUsers> userOptional = ourUserRepo.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
    }

    @Override
    public OurUsers addUser(OurUsers user) {
        return ourUserRepo.save(user);
    }

    @Override
    public void removeUser(int userId) {
        ourUserRepo.deleteById(userId);
    }

    public OurUsers modifyUser(OurUsers user) {
        Optional<OurUsers> existingUserOptional = ourUserRepo.findById(user.getId());
        if (existingUserOptional.isPresent()) {
            OurUsers existingUser = existingUserOptional.get();
            existingUser.setName(user.getName());
            existingUser.setRole(user.getRole());
            existingUser.setPassword(user.getPassword());
            existingUser.setEmail(user.getEmail());
            existingUser.setFichesEtu(user.getFichesEtu()); // Met à jour les fiches des étudiants
            existingUser.setConvention(user.getConvention());
            return ourUserRepo.save(existingUser);
        } else {
            throw new NoSuchElementException("User with ID " + user.getId() + " does not exist.");
        }
    }


    public OurUsers getUserById(int userId) {
        Optional<OurUsers> userOptional = ourUserRepo.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
    }

    @Override
    public void affecterConventionAUneUserExistante(int userId, Convention convention) {
        OurUsers user = getUserById(userId);
        user.setConvention(convention);
        ourUserRepo.save(user);
    }
    public List<OurUsers> findUsersWithoutFiche() {
        return ourUserRepo.findUsersWithoutFiche();
    }
}



