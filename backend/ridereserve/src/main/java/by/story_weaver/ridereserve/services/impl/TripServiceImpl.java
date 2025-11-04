package by.story_weaver.ridereserve.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.repositories.TripRepository;
import by.story_weaver.ridereserve.services.TripService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TripServiceImpl implements TripService{

    @Autowired
    private TripRepository tripRepository;

    @Override
    public List<Trip> getAllTrips() {
        try {
            return tripRepository.findAll();
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
            return tripRepository.save(trip);
        } catch (Exception e) {
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
            tripRepository.deleteById(id);
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
}
