package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.TripDao;
import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TripRepository;

@Singleton
public class TripRepositoryImpl implements TripRepository {
    private final TripDao dao;

    @Inject
    public TripRepositoryImpl(TripDao dao) { this.dao = dao; }

    @Override
    public void addTrip(Trip trip) { dao.addTrip(trip); }
    @Override
    public void updateStatusTrip(long tripId, TripStatus status) {
        dao.updateTripStatus(tripId, status);
    }
    @Override
    public void updateTrip(Trip trip) {
        dao.updateTrip(trip);
    }
    @Override
    public void removeTrip(long id) { dao.removeTrip(id); }
    @Override
    public Trip getTrip(long id) { return dao.getTrip(id); }
    @Override
    public List<Trip> getTripsByRoute(long routeId) { return dao.getTripsByRoute(routeId); }
    @Override public List<Trip> getTripsByDriver(int driverId) { return dao.getTripsByDriver(driverId); }
    @Override public List<Trip> getAll() { return dao.getAll(); }
}
