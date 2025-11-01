package by.story_weaver.ridereserve.controllers;

import by.story_weaver.ridereserve.models.*;
import by.story_weaver.ridereserve.services.*;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private TripService tripService;

    @Autowired
    private BookingService bookingService;
    
    @GetMapping("/stats")
public ResponseEntity<AdminStats> getAdminStats() {
    try {
        AdminStats stats = adminService.getAdminStats();
        return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.noContent().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/activetrips")
public ResponseEntity<List<Trip>> getActiveTrips() {
    try {
        List<Trip> trips = adminService.getActiveTrips();
        return trips != null ? ResponseEntity.ok(trips) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

// USERS
@GetMapping("/users")
public ResponseEntity<List<User>> getAllUsers() {
    try {
        List<User> users = userService.getAllUsers();
        return users != null ? ResponseEntity.ok(users) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/users/{id}")
public ResponseEntity<User> getUserById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) {
    try {
        if (user == null) return ResponseEntity.badRequest().build();
        User createdUser = userService.createUser(user);
        return createdUser != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
                : ResponseEntity.badRequest().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/users/{id}")
public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    try {
        if (id == null || user == null) return ResponseEntity.badRequest().build();
        if (user.getId() != null && !user.getId().equals(id)) return ResponseEntity.badRequest().build();
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

// ROUTES
@GetMapping("/routes")
public ResponseEntity<List<Route>> getAllRoutes() {
    try {
        List<Route> routes = routeService.getAllRoutes();
        return routes != null ? ResponseEntity.ok(routes) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/routes/{id}")
public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        Route route = routeService.getRouteById(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping("/routes")
public ResponseEntity<Route> createRoute(@RequestBody Route route) {
    try {
        if (route == null) return ResponseEntity.badRequest().build();
        Route createdRoute = routeService.createRoute(route);
        return createdRoute != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdRoute)
                : ResponseEntity.badRequest().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/routes/{id}")
public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody Route route) {
    try {
        if (id == null || route == null) return ResponseEntity.badRequest().build();
        if (route.getId() != null && !route.getId().equals(id)) return ResponseEntity.badRequest().build();
        route.setId(id);
        Route updatedRoute = routeService.updateRoute(route);
        return updatedRoute != null ? ResponseEntity.ok(updatedRoute) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@DeleteMapping("/routes/{id}")
public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        boolean deleted = routeService.deleteRoute(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

// VEHICLES
@GetMapping("/vehicles")
public ResponseEntity<List<Vehicle>> getAllVehicles() {
    try {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return vehicles != null ? ResponseEntity.ok(vehicles) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/vehicles/{id}")
public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return vehicle != null ? ResponseEntity.ok(vehicle) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping("/vehicles")
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

@PutMapping("/vehicles/{id}")
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

@DeleteMapping("/vehicles/{id}")
public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        boolean deleted = vehicleService.deleteVehicle(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

// TRIPS
@GetMapping("/trips")
public ResponseEntity<List<Trip>> getAllTrips() {
    try {
        List<Trip> trips = tripService.getAllTrips();
        return trips != null ? ResponseEntity.ok(trips) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/trips/{id}")
public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        Trip trip = tripService.getTripById(id);
        return trip != null ? ResponseEntity.ok(trip) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping("/trips")
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

@PutMapping("/trips/{id}")
public ResponseEntity<Trip> updateTrip(@PathVariable Long id, @RequestBody Trip trip) {
    try {
        if (id == null || trip == null) return ResponseEntity.badRequest().build();
        if (trip.getId() != null && !trip.getId().equals(id)) return ResponseEntity.badRequest().build();
        trip.setId(id);
        Trip updatedTrip = tripService.updateTrip(trip);
        return updatedTrip != null ? ResponseEntity.ok(updatedTrip) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@DeleteMapping("/trips/{id}")
public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        boolean deleted = tripService.deleteTrip(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

// BOOKINGS
@GetMapping("/bookings")
public ResponseEntity<List<Booking>> getAllBookings() {
    try {
        List<Booking> bookings = bookingService.getAllBookings();
        return bookings != null ? ResponseEntity.ok(bookings) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/bookings/{id}")
public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        Booking booking = bookingService.getBookingById(id);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping("/bookings")
public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
    try {
        if (booking == null) return ResponseEntity.badRequest().build();
        Booking createdBooking = bookingService.createBooking(booking);
        return createdBooking != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdBooking)
                : ResponseEntity.badRequest().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/bookings/{id}")
public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking booking) {
    try {
        if (id == null || booking == null) return ResponseEntity.badRequest().build();
        if (booking.getId() != null && !booking.getId().equals(id)) return ResponseEntity.badRequest().build();
        booking.setId(id);
        Booking updatedBooking = bookingService.updateBooking(booking);
        return updatedBooking != null ? ResponseEntity.ok(updatedBooking) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@DeleteMapping("/bookings/{id}")
public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        boolean deleted = bookingService.deleteBooking(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/check")
public String check() {
    return "ok";
}

    
}