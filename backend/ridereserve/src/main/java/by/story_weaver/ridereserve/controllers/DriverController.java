package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    try {
        if (driverId == null) return ResponseEntity.badRequest().build();
        User driver = userService.getUserById(driverId);
        return driver != null ? ResponseEntity.ok(driver) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/{driverId}/profile")
public ResponseEntity<User> updateDriverProfile(@PathVariable Long driverId, @RequestBody User request) {
    try {
        if (driverId == null || request == null) return ResponseEntity.badRequest().build();
        if (request.getId() != null && !request.getId().equals(driverId)) return ResponseEntity.badRequest().build();
        request.setId(driverId);
        User updatedDriver = userService.updateUser(request);
        return updatedDriver != null ? ResponseEntity.ok(updatedDriver) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/{driverId}/trips")
public ResponseEntity<List<Trip>> getDriverTrips(@PathVariable Long driverId) {
    try {
        if (driverId == null) return ResponseEntity.badRequest().build();
        List<Trip> trips = tripService.getDriverTrips(driverId);
        return trips != null ? ResponseEntity.ok(trips) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/trips/{tripId}/status")
public ResponseEntity<Trip> updateTripStatus(@PathVariable Long tripId, @RequestParam TripStatus status) {
    try {
        if (tripId == null || status == null) return ResponseEntity.badRequest().build();
        Trip trip = tripService.updateTripStatus(tripId, status);
        return trip != null ? ResponseEntity.ok(trip) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

}
