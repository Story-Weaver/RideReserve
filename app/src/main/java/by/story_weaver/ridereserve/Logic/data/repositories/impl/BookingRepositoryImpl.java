package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.BookingDao;
import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.BookingRepository;

@Singleton
public class BookingRepositoryImpl implements BookingRepository {
    private final BookingDao dao;

    @Inject
    public BookingRepositoryImpl(BookingDao dao) { this.dao = dao; }

    @Override
    public void addBooking(Booking booking) { dao.addBooking(booking); }
    @Override
    public void updateBooking(Booking booking) {
        dao.updateBooking(booking);
    }
    @Override
    public void updateStatusBooking(long bookingId, BookingStatus status) {
        dao.updateBookingStatus(bookingId, status);
    }
    @Override
    public void removeBooking(long id) { dao.removeBooking(id); }
    @Override
    public Booking getBooking(long id) { return dao.getBooking(id); }
    @Override
    public List<Booking> getBookingsByTrip(long tripId) { return dao.getBookingsByTrip(tripId); }
    @Override
    public List<Booking> getAll() { return dao.getAll(); }
    @Override
    public boolean hasBookingForUserAndTrip(long passengerId, long tripId) {
        return dao.hasBookingForUserAndTrip(passengerId, tripId);
    }
}

