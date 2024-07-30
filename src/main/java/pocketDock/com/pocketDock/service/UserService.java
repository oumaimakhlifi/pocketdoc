package pocketDock.com.pocketDock.service;

import pocketDock.com.pocketDock.entity.Convention;
import pocketDock.com.pocketDock.entity.OurUsers;

import java.util.List;

public interface UserService {
    boolean checkEmail(String email);
    public List<OurUsers> getAllUsers();
    public List<OurUsers> getUsers();
    public OurUsers retrieveUser(int userId);
    public OurUsers addUser(OurUsers user);
    public void removeUser(int userId);
    public OurUsers modifyUser(OurUsers user);
    public OurUsers getUserById(int userId);
    public void affecterConventionAUneUserExistante(int userId, Convention convention);
    public List<OurUsers> findUsersWithoutFiche();

}
