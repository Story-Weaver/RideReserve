package by.story_weaver.ridereserve.services;

import java.util.List;

import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.enums.TripStatus;

public interface TripService {
    public List<Trip> getAllTrips();
    public Trip getTripById(long id);
    public Trip createTrip(Trip trip);
    public Trip updateTrip(Trip trip);
    public boolean deleteTrip(long id);

    public Trip updateTripStatus(long tripId, TripStatus status);
    public List<Trip> getTripsByRoute(long routeId);
    public List<Trip> getTripsByStatus(TripStatus status);
    public List<Trip> getActiveTrips(int count);
    public List<Trip> getDriverTrips(long driverId);
    public List<Trip> getTripsByVehicle(long vehicleId);
}
