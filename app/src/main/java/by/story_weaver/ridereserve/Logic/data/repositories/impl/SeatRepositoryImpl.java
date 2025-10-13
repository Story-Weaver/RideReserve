package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.SeatDao;
import by.story_weaver.ridereserve.Logic.data.models.Seat;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.SeatRepository;

@Singleton
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatDao dao;

    @Inject
    public SeatRepositoryImpl(SeatDao dao) { this.dao = dao; }

    @Override
    public void addSeat(Seat seat) { dao.addSeat(seat); }

    @Override
    public void removeSeat(int id) { dao.removeSeat(id); }

    @Override
    public Seat getSeat(int id) { return dao.getSeat(id); }

    @Override
    public List<Seat> getSeatsByVehicle(long vehicleId) { return dao.getSeatsByVehicle(vehicleId); }
}

