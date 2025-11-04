package by.story_weaver.ridereserve.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.User;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.models.enums.UserRole;
import by.story_weaver.ridereserve.repositories.UserRepository;
import by.story_weaver.ridereserve.services.BookingService;
import by.story_weaver.ridereserve.services.TripService;
import by.story_weaver.ridereserve.services.UserService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TripService tripService;

    @Override
    public List<User> getAllUsers() {
        try {
            List<User> list = userRepository.findAll();
            List<User> finalList = new ArrayList<>();
            for (User user : list) {
                if(!user.getDeleted()){
                    finalList.add(user);
                }
            }
            return finalList;
        } catch (Exception e) {
            return null;
        }
    }
   
    @Override
    public User getUserById(long id) {
        try {
            return userRepository.getUserById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User createUser(User user) {
        try {
            userRepository.save(user);
            return userRepository.findByEmail(user.getEmail());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteUser(long id) {
        try {
            User user = userRepository.getUserById(id);
            if(user.getRole().equals(UserRole.ADMIN)){
                return false;
            } else if(user.getRole().equals(UserRole.DRIVER)){
                List<Trip> list = tripService.getDriverTrips(id);
                if(list != null){
                    for (Trip trip : list) {
                        List<Booking> list2 = bookingService.getBookingsByTrip(trip.getId());
                        if(list2 != null){
                            for (Booking booking : list2) {
                                bookingService.updateBookingStatus(booking.getId(), BookingStatus.CANCELLED);
                                bookingService.deleteBooking(booking.getId());
                            }
                        }
                        tripService.updateTripStatus(trip.getId(), TripStatus.CANCELLED);
                        tripService.deleteTrip(trip.getId());
                    }
                }
            } else {
                List<Booking> list = bookingService.getBookingsByUser(id);
                if(list != null){
                    for (Booking booking : list) {
                        bookingService.updateBookingStatus(booking.getId(), BookingStatus.CANCELLED);
                        bookingService.deleteBooking(booking.getId());
                    }
                }
            }
            user.setDeleted(true);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
