package by.story_weaver.ridereserve.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.repositories.TripRepository;
import by.story_weaver.ridereserve.services.BookingService;
import by.story_weaver.ridereserve.services.TripService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TripServiceImpl implements TripService{

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BookingService bookingService;

    @Override
    public List<Trip> getAllTrips() {
        try {
            List<Trip> list = tripRepository.findAll();
            List<Trip> finalList = new ArrayList<>();
            for (Trip trip : list) {
                if(!trip.getDeleted()){
                    finalList.add(trip);
                }
            }
            return finalList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Trip getTripById(long id) {
        try {
            return tripRepository.getTripById(id);
        } catch (Exception e) {
            
            return null;
        }
    }

    @Override
    public Trip createTrip(Trip trip) {
        try {
            trip.setId(null);
            Trip saved = tripRepository.save(trip);
            System.out.println("Trip saved successfully: " + saved);
            return saved;
        } catch (Exception e) {
            System.err.println("Error saving trip: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Trip updateTrip(Trip trip) {
        try {
            return tripRepository.save(trip);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteTrip(long id) {
        try {
            List<Booking> list = bookingService.getBookingsByTrip(id);
            if(list != null){
                for (Booking booking : list) {
                    bookingService.updateBookingStatus(booking.getId(), BookingStatus.CANCELLED);
                    bookingService.deleteBooking(booking.getId());
                }
            }
            updateTripStatus(id, TripStatus.CANCELLED);
            Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));
            trip.setDeleted(true);
            tripRepository.save(trip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Trip updateTripStatus(long tripId, TripStatus status) {
        try {
            Trip trip = tripRepository.getTripById(tripId);
            trip.setStatus(status);
            return tripRepository.save(trip);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Trip> getTripsByRoute(long routeId) {
        try {
            return tripRepository.findByRouteId(routeId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Trip> getTripsByStatus(TripStatus status) {
        try {
            return tripRepository.findByStatus(status.toString());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Trip> getActiveTrips(int count) {
        try {
            return tripRepository.findActiveTrips(count);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Trip> getDriverTrips(long driverId) {
        try {
            return tripRepository.findByDriverId(driverId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Trip> getTripsByVehicle(long vehicleId){
        try {
            return tripRepository.findByVehicleId(vehicleId);
        } catch (Exception e) {
            return null;
        }
    }
}
