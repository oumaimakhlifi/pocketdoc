package pocketDock.com.pocketDock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pocketDock.com.pocketDock.repository.FicheRepository;
import pocketDock.com.pocketDock.repository.SymptomRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticService {

    private final FicheRepository ficheRepository;
    private final SymptomRepository symptomRepository;
    @Autowired
    public StatisticService(FicheRepository ficheRepository,SymptomRepository symptomRepository) {
        this.ficheRepository = ficheRepository;
        this.symptomRepository = symptomRepository;
    }

    public Map<String, Long> getFicheCountByTypeAddiction() {
        Map<String, Long> addictionCounts = new HashMap<>();
        addictionCounts.put("ALCOOL", ficheRepository.countFichesAlcool());
        addictionCounts.put("TABAC", ficheRepository.countFichesTabac());
        addictionCounts.put("DROGUE", ficheRepository.countFichesDrogue());
        return addictionCounts;
    }
    public Map<String, Long> getSymptomCountsByIntensity() {
        Map<String, Long> symptomCountsByIntensity = new HashMap<>();
        symptomCountsByIntensity.put("HIGH", symptomRepository.countSymptomsHighIntensity());
        symptomCountsByIntensity.put("MODERATE", symptomRepository.countSymptomsModerateIntensity());
        symptomCountsByIntensity.put("WEAK", symptomRepository.countSymptomsWeakIntensity());
        return symptomCountsByIntensity;
    }



}
