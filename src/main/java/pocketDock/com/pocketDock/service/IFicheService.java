package pocketDock.com.pocketDock.service;

import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.OurUsers;

import java.util.List;

public interface IFicheService {
    public List<Fiche> getFiches();
    public Fiche retrieveFiche(Long ficheId);
    Fiche addFiche(Fiche fiche);
    void removeFiche(Long ficheId);
    Fiche modifyFiche(Fiche fiche);
    public Long getNextFicheId();
    public Fiche addFicheAndAssignFicheToUserAndDoctor(Fiche fiche, int userId1, int userId2);
    public int findEtudiantIdByFicheId(Long ficheId);
    public Fiche getLatestFicheByEtudiant(OurUsers etudiant);
}