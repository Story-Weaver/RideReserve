package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.User;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.services.TripService;
import by.story_weaver.ridereserve.services.UserService;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
@AllArgsConstructor
public class DriverController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private TripService tripService;
    
    @GetMapping("/{driverId}/profile")
    public ResponseEntity<User> getDriverProfile(@PathVariable Long driverId) {
        User driver = userService.getUserById(driverId);
        return driver != null ? ResponseEntity.ok(driver) : ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{driverId}/profile")
    public ResponseEntity<User> updateDriverProfile(@PathVariable Long driverId, @RequestBody User request) {
        User updatedDriver = userService.updateUser(request);
        return updatedDriver != null ? ResponseEntity.ok(updatedDriver) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{driverId}/trips")
    public ResponseEntity<List<Trip>> getDriverTrips(@PathVariable Long driverId) {
        List<Trip> trips = tripService.getDriverTrips(driverId);
        return ResponseEntity.ok(trips);
    }
    
    @PutMapping("/trips/{tripId}/status")
    public ResponseEntity<Trip> updateTripStatus(@PathVariable Long tripId, @RequestParam TripStatus status) {
        Trip trip = tripService.updateTripStatus(tripId, status);
        return trip != null ? ResponseEntity.ok(trip) : ResponseEntity.notFound().build();
    }
}
