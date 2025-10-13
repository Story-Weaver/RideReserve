package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.Vehicle;

import java.util.List;

public interface VehicleRepository {
    void addVehicle(Vehicle vehicle);
    void removeVehicle(int id);
    Vehicle getVehicle(int id);
    List<Vehicle> getAllVehicles();
}

