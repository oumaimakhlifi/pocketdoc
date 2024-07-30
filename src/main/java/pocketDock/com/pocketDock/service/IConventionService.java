package pocketDock.com.pocketDock.service;

import pocketDock.com.pocketDock.entity.Convention;

import java.util.List;

public interface IConventionService {


    public List<Convention> getConventions();
    public Convention retrieveConvention(Long conventionId);
    public Convention addConvention(Convention convention);
    public void removeConvention(Long conventionId);
    public Convention modifyConvention(long conventionId, Convention updatedConventionData);
    public Convention addConventionAndAssignConventionToDoctor(Convention convention, int doctorId);
    public Convention DesaffecterMedecinFromConvention(Long conventionId);

    public Convention modifyConvention2(long conventionId, Convention updatedConventionData);


}
