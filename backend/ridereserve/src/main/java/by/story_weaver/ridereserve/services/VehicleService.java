package by.story_weaver.ridereserve.services;

import java.util.List;

import by.story_weaver.ridereserve.models.Vehicle;

public interface VehicleService {
    public List<Vehicle> getAllVehicles();
    public Vehicle getVehicleById(long id);
    public Vehicle createVehicle(Vehicle vehicle);
    public Vehicle updateVehicle(long id, Vehicle vehicle);
    public boolean deleteVehicle(long id);
}
