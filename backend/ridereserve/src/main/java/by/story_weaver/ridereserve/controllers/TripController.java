package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.services.BookingService;
import by.story_weaver.ridereserve.services.TripService;
import lombok.AllArgsConstructor;
import java.util.List;


@RestController
@RequestMapping("/api/trips")
@AllArgsConstructor
public class TripController {
    
    @Autowired
    private TripService tripService;

    @Autowired
    private BookingService bookingService;
    
    @GetMapping
public ResponseEntity<List<Trip>> getAllTrips() {
    try {
        List<Trip> trips = tripService.getAllTrips();
        return trips != null ? ResponseEntity.ok(trips) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/{id}")
public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        Trip trip = tripService.getTripById(id);
        return trip != null ? ResponseEntity.ok(trip) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/route/{routeId}")
public ResponseEntity<List<Trip>> getTripsByRoute(@PathVariable Long routeId) {
    try {
        if (routeId == null) return ResponseEntity.badRequest().build();
        List<Trip> trips = tripService.getTripsByRoute(routeId);
        return trips != null ? ResponseEntity.ok(trips) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/status/{status}")
public ResponseEntity<List<Trip>> getTripsByStatus(@PathVariable TripStatus status) {
    try {
        if (status == null) return ResponseEntity.badRequest().build();
        List<Trip> trips = tripService.getTripsByStatus(status);
        return trips != null ? ResponseEntity.ok(trips) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/active/{count}")
public ResponseEntity<List<Trip>> getActiveTrips(@PathVariable int count) {
    try {
        if (count <= 0) return ResponseEntity.badRequest().build();
        List<Trip> trips = tripService.getActiveTrips(count);
        return trips != null ? ResponseEntity.ok(trips) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping
public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {
    try {
        if (trip == null) return ResponseEntity.badRequest().build();
        Trip createdTrip = tripService.createTrip(trip);
        return createdTrip != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdTrip)
                : ResponseEntity.badRequest().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/passangers")
public ResponseEntity<?> getPassangers() {
    try {
        return null;
    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}


@PutMapping("/")
public ResponseEntity<Trip> updateTrip(@RequestBody Trip trip) {
    try {
        if (trip == null || trip.getId() == null) return ResponseEntity.badRequest().build();
        Trip createdTrip = tripService.updateTrip(trip);
        return createdTrip != null ? ResponseEntity.ok(createdTrip) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/{id}/status")
public ResponseEntity<Trip> updateTripStatus(@PathVariable Long id, @RequestParam TripStatus status) {
    try {
        if (id == null || status == null) return ResponseEntity.badRequest().build();
        Trip updatedTrip = tripService.updateTripStatus(id, status);
        if (updatedTrip == null) return ResponseEntity.notFound().build();

        // Обновляем статусы бронирований только если trip обновился успешно
        List<Booking> bookings = bookingService.getBookingsByTrip(updatedTrip.getId());
        if (bookings != null) {
            for (Booking i : bookings) {
                bookingService.updateBookingStatus(i.getId(), mapTripStatusToBookingStatus(status));
            }
        }
        return ResponseEntity.ok(updatedTrip);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        boolean deleted = tripService.deleteTrip(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}


    private BookingStatus mapTripStatusToBookingStatus(TripStatus tripStatus) {
        switch (tripStatus) {
            case CANCELLED:
                return BookingStatus.CANCELLED;
            case COMPLETED:
                return BookingStatus.COMPLETED;
            default:
                return BookingStatus.CONFIRMED;
        }
    }
}