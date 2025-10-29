package by.story_weaver.ridereserve.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Vehicle;
import by.story_weaver.ridereserve.repositories.VehicleRepository;
import by.story_weaver.ridereserve.services.VehicleService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VehicleServiceImpl implements VehicleService{

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getAllVehicles() {
        try {
            return vehicleRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Vehicle getVehicleById(long id) {
        try {
            return vehicleRepository.getVehicleById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        try {
            vehicleRepository.save(vehicle);
            return vehicleRepository.getVehicleByNum(vehicle.getPlateNumber());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Vehicle updateVehicle(long id, Vehicle vehicle) {
        try {
            vehicleRepository.save(vehicle);
            return vehicleRepository.getVehicleByNum(vehicle.getPlateNumber());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteVehicle(long id) {
        try {
            vehicleRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
}
