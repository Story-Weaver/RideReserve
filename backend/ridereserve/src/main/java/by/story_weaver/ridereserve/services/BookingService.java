package by.story_weaver.ridereserve.services;

import java.util.List;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.enums.BookingStatus;

public interface BookingService {
    public List<Booking> getAllBookings();
    public Booking getBookingById(long bookingId);
    public Booking createBooking(Booking booking);
    public Booking updateBooking(Booking booking);
    public boolean deleteBooking(long bookingId);

    public List<Booking> getBookingsByUser(long userId);
    public List<Booking> getBookingsByTrip(long tripId);
    public List<Booking> getBookingsByStatus(BookingStatus status);
    public boolean checkBookingExists(long userId, long tripId);
    public Booking updateBookingStatus(long bookingId, BookingStatus status);
}
