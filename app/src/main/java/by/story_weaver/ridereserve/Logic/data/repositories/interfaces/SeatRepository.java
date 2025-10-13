package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.Seat;

import java.util.List;

public interface SeatRepository {
    void addSeat(Seat seat);
    void removeSeat(int id);
    Seat getSeat(int id);
    List<Seat> getSeatsByVehicle(long vehicleId);
}

