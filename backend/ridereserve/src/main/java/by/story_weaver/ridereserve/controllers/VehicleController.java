package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import by.story_weaver.ridereserve.models.Vehicle;
import by.story_weaver.ridereserve.services.VehicleService;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@AllArgsConstructor
public class VehicleController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @GetMapping
public ResponseEntity<List<Vehicle>> getAllVehicles() {
    try {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return vehicles != null ? ResponseEntity.ok(vehicles) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/{id}")
public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return vehicle != null ? ResponseEntity.ok(vehicle) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping
public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
    try {
        if (vehicle == null) return ResponseEntity.badRequest().build();
        Vehicle createdVehicle = vehicleService.createVehicle(vehicle);
        return createdVehicle != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle)
                : ResponseEntity.badRequest().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/{id}")
public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
    try {
        if (id == null || vehicle == null) return ResponseEntity.badRequest().build();
        if (vehicle.getId() != null && !vehicle.getId().equals(id)) return ResponseEntity.badRequest().build();
        vehicle.setId(id);
        Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);
        return updatedVehicle != null ? ResponseEntity.ok(updatedVehicle) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        boolean deleted = vehicleService.deleteVehicle(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

}