package pocketDock.com.pocketDock.service;

import pocketDock.com.pocketDock.entity.Fiche;
import pocketDock.com.pocketDock.entity.Symptom;

import java.util.List;

public interface ISymptomService {
    List<Symptom> getSymptoms();

    Symptom retrieveSymptom(Long symptomId);

    public Symptom addSymptomAndAssignFicheToSymptom(Symptom symptom, Long ficheid,int userid);

    void removeSymptom(Long symptomId);

    Symptom modifySymptom(Symptom symptom);


    public int countUnseenSymptomsForFiche(Long ficheId);
    public Fiche DesaffecterSymptomFromFiche(Long sympId);
    public List<Symptom> findSymptomsByMyFiche(Fiche fiche);

}
