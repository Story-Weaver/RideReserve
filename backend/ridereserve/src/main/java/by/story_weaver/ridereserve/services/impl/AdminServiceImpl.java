package by.story_weaver.ridereserve.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.AdminStats;
import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.models.enums.UserRole;
import by.story_weaver.ridereserve.repositories.BookingRepository;
import by.story_weaver.ridereserve.repositories.RouteRepository;
import by.story_weaver.ridereserve.repositories.TripRepository;
import by.story_weaver.ridereserve.repositories.UserRepository;
import by.story_weaver.ridereserve.repositories.VehicleRepository;
import by.story_weaver.ridereserve.services.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired 
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Override
    public AdminStats getAdminStats(){
        try {
            AdminStats stats = new AdminStats();
            
            stats.setTotalUsers(userRepository.count());
            stats.setTotalRoutes(routeRepository.count());
            stats.setTotalTrips(tripRepository.count());
            stats.setTotalBookings(bookingRepository.count());
            stats.setTotalVehicles(vehicleRepository.count());
            stats.setTotalDrivers(userRepository.countByRole(UserRole.DRIVER));
            stats.setActiveTrips(tripRepository.countByStatus(TripStatus.IN_PROGRESS));
            stats.setScheduledTrips(tripRepository.countByStatus(TripStatus.SCHEDULED));
            stats.setConfirmedBookings(bookingRepository.countByStatus(BookingStatus.CONFIRMED));
            stats.setCancelledBookings(bookingRepository.countByStatus(BookingStatus.CANCELLED));
            return stats;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Trip> getActiveTrips(){
        try {
            return tripRepository.findByStatus(TripStatus.IN_PROGRESS);
        } catch (Exception e) {
            return null;
        }
    }
}
