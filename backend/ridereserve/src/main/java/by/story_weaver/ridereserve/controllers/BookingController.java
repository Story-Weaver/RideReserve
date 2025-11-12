package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.services.BookingService;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Collections;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/bookings")
@AllArgsConstructor
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            if (bookings == null || bookings.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid booking ID");
            }
            
            Booking booking = bookingService.getBookingById(id);
            if (booking != null) {
                return ResponseEntity.ok(booking);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Booking not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving booking: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUser(@PathVariable Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body("Invalid user ID");
            }
            
            List<Booking> bookings = bookingService.getBookingsByUser(userId);
            if (bookings == null || bookings.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user bookings: " + e.getMessage());
        }
    }
    
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<?> getBookingsByTrip(@PathVariable Long tripId) {
        try {
            if (tripId == null || tripId <= 0) {
                return ResponseEntity.badRequest().body("Invalid trip ID");
            }
            
            List<Booking> bookings = bookingService.getBookingsByTrip(tripId);
            if (bookings == null || bookings.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving trip bookings: " + e.getMessage());
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getBookingsByStatus(@PathVariable BookingStatus status) {
        try {
            if (status == null) {
                return ResponseEntity.badRequest().body("Invalid booking status");
            }
            
            List<Booking> bookings = bookingService.getBookingsByStatus(status);
            if (bookings == null || bookings.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving bookings by status: " + e.getMessage());
        }
    }
    
    @GetMapping("/exists/{passengerId}/{tripId}")
    public ResponseEntity<?> checkBookingExists(
            @PathVariable Long passengerId, 
            @PathVariable Long tripId) {
        try {
            if (passengerId == null || passengerId <= 0) {
                return ResponseEntity.badRequest().body("Invalid passenger ID");
            }
            if (tripId == null || tripId <= 0) {
                return ResponseEntity.badRequest().body("Invalid trip ID");
            }
            
            boolean exists = bookingService.checkBookingExists(passengerId, tripId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking booking existence: " + e.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {    
        try {
            if (booking == null) {
                return ResponseEntity.badRequest().body("Booking data is required");
            }
            if (booking.getTripId() <= 0) {
                return ResponseEntity.badRequest().body("Valid trip ID is required");
            }
            if (booking.getPassengerId() <= 0) {
                return ResponseEntity.badRequest().body("Valid passenger ID is required");
            }
            if (booking.getPrice() == null || booking.getPrice().doubleValue() <= 0) {
                return ResponseEntity.badRequest().body("Valid price is required");
            }
            
            // Убедимся, что ID null для новой записи
            booking.setId(null);
            
            Booking createdBooking = bookingService.createBooking(booking);
            if (createdBooking != null && createdBooking.getId() != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to create booking - returned null");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating booking: " + e.getMessage());
        }
    }
    
    @PutMapping()
    public ResponseEntity<?> updateBooking(@RequestBody Booking booking) {
        try {
            if (booking == null) {
                return ResponseEntity.badRequest().body("Booking data is required");
            }
            if (booking.getId() == null || booking.getId() <= 0) {
                return ResponseEntity.badRequest().body("Valid booking ID is required for update");
            }
            
            Booking updatedBooking = bookingService.updateBooking(booking);
            if (updatedBooking != null) {
                return ResponseEntity.ok(updatedBooking);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Booking not found or update failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating booking: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid booking ID");
            }
            if (status == null) {
                return ResponseEntity.badRequest().body("Valid booking status is required");
            }
            
            Booking updatedBooking = bookingService.updateBookingStatus(id, status);
            if (updatedBooking != null) {
                return ResponseEntity.ok(updatedBooking);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Booking not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating booking status: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid booking ID");
            }
            
            boolean deleted = bookingService.deleteBooking(id);
            if (deleted) {
                return ResponseEntity.ok().body("Booking deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Booking not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting booking: " + e.getMessage());
        }
    }
}