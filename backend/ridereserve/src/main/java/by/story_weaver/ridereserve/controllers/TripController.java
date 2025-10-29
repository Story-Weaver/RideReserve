package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.services.TripService;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@AllArgsConstructor
public class TripController {
    
    @Autowired
    private TripService tripService;
    
    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trips = tripService.getAllTrips();
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        Trip trip = tripService.getTripById(id);
        return trip != null ? ResponseEntity.ok(trip) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<Trip>> getTripsByRoute(@PathVariable Long routeId) {
        List<Trip> trips = tripService.getTripsByRoute(routeId);
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Trip>> getTripsByStatus(@PathVariable TripStatus status) {
        List<Trip> trips = tripService.getTripsByStatus(status);
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/active/{count}")
    public ResponseEntity<List<Trip>> getActiveTrips(@PathVariable int count) {
        List<Trip> trips = tripService.getActiveTrips(count);
        return ResponseEntity.ok(trips);
    }
    
    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {
        Trip createdTrip = tripService.createTrip(trip);
        return createdTrip != null ? ResponseEntity.ok(createdTrip) : ResponseEntity.badRequest().build();
    }
    
    @PutMapping("/")
    public ResponseEntity<Trip> updateTrip(@RequestBody Trip trip) {
        Trip createdTrip = tripService.updateTrip(trip);
        return createdTrip != null ? ResponseEntity.ok(createdTrip) : ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Trip> updateTripStatus(@PathVariable Long id, @RequestParam TripStatus status) {
        Trip updatedTrip = tripService.updateTripStatus(id, status);
        return updatedTrip != null ? ResponseEntity.ok(updatedTrip) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        boolean deleted = tripService.deleteTrip(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}