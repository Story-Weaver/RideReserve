package by.story_weaver.ridereserve.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.repositories.BookingRepository;
import by.story_weaver.ridereserve.services.BookingService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService{

    @Autowired
    private BookingRepository bookingRepository;
    @Override
    public List<Booking> getAllBookings() {
        try {
            return bookingRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Booking getBookingById(long bookingId) {
        try {
            return bookingRepository.getBokingById(bookingId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Booking createBooking(Booking booking) {
        try {
            return bookingRepository.save(booking);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Booking updateBooking(Booking booking) {
        try {
            return bookingRepository.save(booking);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteBooking(long bookingId) {
        try {
            bookingRepository.deleteById(bookingId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Booking> getBookingsByUser(long userId) {
        try {
            return bookingRepository.findByPassengerId(userId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Booking> getBookingsByTrip(long tripId) {
        try {
            return bookingRepository.findByTripId(tripId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        try {
            return bookingRepository.findByStatus(status.toString());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean checkBookingExists(long userId, long tripId) {
        try {
            return bookingRepository.existsByPassengerAndTrip(userId, tripId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Booking updateBookingStatus(long bookingId, BookingStatus status) {
        try {
            Booking booking = bookingRepository.getBokingById(bookingId);
            booking.setStatus(status);
            return bookingRepository.save(booking);
        } catch (Exception e) {
            return null;
        }
    }
    
}
