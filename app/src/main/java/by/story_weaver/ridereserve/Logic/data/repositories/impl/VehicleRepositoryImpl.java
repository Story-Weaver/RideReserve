package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.VehicleDao;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.VehicleRepository;

@Singleton
public class VehicleRepositoryImpl implements VehicleRepository {
    private final VehicleDao dao;

    @Inject
    public VehicleRepositoryImpl(VehicleDao dao) { this.dao = dao; }

    @Override
    public void addVehicle(Vehicle vehicle) { dao.addVehicle(vehicle); }

    @Override
    public void removeVehicle(int id) { dao.removeVehicle(id); }

    @Override
    public Vehicle getVehicle(int id) { return dao.getVehicle(id); }

    @Override
    public List<Vehicle> getAllVehicles() { return dao.getAllVehicles(); }
}
