package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.Trip;

import java.util.List;

public interface TripRepository {
    void addTrip(Trip trip);
    void removeTrip(int id);
    Trip getTrip(long id);
    List<Trip> getTripsByRoute(long routeId);
    List<Trip> getTripsByDriver(int driverId);
    List<Trip> getAll();
}

