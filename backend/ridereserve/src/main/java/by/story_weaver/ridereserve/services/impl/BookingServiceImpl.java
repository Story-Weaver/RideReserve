package by.story_weaver.ridereserve.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.repositories.BookingRepository;
import by.story_weaver.ridereserve.services.BookingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService{

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public List<Booking> getAllBookings() {
        try {
            List<Booking> list = bookingRepository.findAll();
            List<Booking> finalList = new ArrayList<>();
            for (Booking booking : list) {
                if(!booking.getDeleted()){
                    finalList.add(booking);
                }
            }
            return finalList;
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
        // Убедитесь, что ID устанавливается в null для новой записи
        booking.setId(null);
        
        // Убедитесь, что статус установлен, если он null
        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.PENDING);
        }
        
        Booking savedBooking = bookingRepository.save(booking);
        System.out.println("Booking saved successfully with ID: " + savedBooking.getId());
        return savedBooking;
        
    } catch (Exception e) {
        System.err.println("Error creating booking: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Failed to create booking: " + e.getMessage());
    }
}

    @Override
    public boolean deleteBooking(long bookingId) {
        try {
            updateBookingStatus(bookingId, BookingStatus.CANCELLED);
            Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
            booking.setDeleted(true);
            bookingRepository.save(booking);
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
    @Transactional
    public Booking updateBookingStatus(long bookingId, BookingStatus status) {
        try {
            Optional<Booking> maybeBooking = bookingRepository.findById(bookingId);
            if (maybeBooking.isEmpty()) {
                System.err.println("updateBookingStatus: booking not found, id=" + bookingId);
                return null;
            }

            Booking booking = maybeBooking.get();
            booking.setStatus(status);
            Booking saved = bookingRepository.save(booking);
            System.out.println("updateBookingStatus: booking updated, id=" + saved.getId() + ", status=" + saved.getStatus());
            return saved;
        } catch (Exception e) {
            System.err.println("updateBookingStatus: error updating booking id=" + bookingId + " -> " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public Booking updateBooking(Booking booking) {
        if (booking == null || booking.getId() == null) {
            System.err.println("updateBooking: invalid booking passed");
            return null;
        }
        try {
            return bookingRepository.save(booking);
        } catch (Exception e) {
            System.err.println("updateBooking: error saving booking id=" + booking.getId() + " -> " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
}
