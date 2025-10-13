package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.TripDao;
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
    public void removeTrip(int id) { dao.removeTrip(id); }

    @Override
    public Trip getTrip(int id) { return dao.getTrip(id); }

    @Override
    public List<Trip> getTripsByRoute(int routeId) { return dao.getTripsByRoute(routeId); }

    @Override public List<Trip> getTripsByDriver(int driverId) { return dao.getTripsByDriver(driverId); }
    @Override public List<Trip> getAll() { return dao.getAll(); }
}
