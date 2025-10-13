package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.Booking;

import java.util.List;

public interface BookingRepository {
    void addBooking(Booking booking);
    void removeBooking(int id);
    Booking getBooking(int id);
    List<Booking> getBookingsByTrip(int tripId);
    List<Booking> getAll();
}
