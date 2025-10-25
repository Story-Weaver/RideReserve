package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;

import java.util.List;

public interface BookingRepository {
    void addBooking(Booking booking);
    void updateBooking(Booking booking);
    void updateStatusBooking(long bookingId, BookingStatus status);
    void removeBooking(long id);
    Booking getBooking(long id);
    List<Booking> getBookingsByTrip(long tripId);
    List<Booking> getAll();
    boolean hasBookingForUserAndTrip(long passengerId, long tripId);
}
