package by.story_weaver.ridereserve.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.Vehicle;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.repositories.VehicleRepository;
import by.story_weaver.ridereserve.services.BookingService;
import by.story_weaver.ridereserve.services.TripService;
import by.story_weaver.ridereserve.services.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VehicleServiceImpl implements VehicleService{

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TripService tripService;

    @Autowired 
    private BookingService bookingService;

    @Override
    public List<Vehicle> getAllVehicles() {
        try {
            List<Vehicle> list = vehicleRepository.findAll();
            List<Vehicle> finalList = new ArrayList<>();
            for (Vehicle vehicle : list) {
                if(!vehicle.getDeleted()){
                    finalList.add(vehicle);
                }
            }
            return finalList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Vehicle getVehicleById(long id) {
        try {
            return vehicleRepository.getVehicleById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        try {
            vehicleRepository.save(vehicle);
            return vehicleRepository.getVehicleByNum(vehicle.getPlateNumber());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Vehicle updateVehicle(long id, Vehicle vehicle) {
        try {
            vehicleRepository.save(vehicle);
            return vehicleRepository.getVehicleByNum(vehicle.getPlateNumber());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteVehicle(long id) {
        try {
            List<Trip> allTripsByVehicle = null;
            allTripsByVehicle = tripService.getTripsByVehicle(id);
            if (allTripsByVehicle != null) {
                for (Trip trip : allTripsByVehicle) {
                    List<Booking> allBookingsByTrip = null;
                    allBookingsByTrip = bookingService.getBookingsByTrip(trip.getId());
                    if(allBookingsByTrip != null){
                        for (Booking booking : allBookingsByTrip) {
                            bookingService.updateBookingStatus(booking.getId(), BookingStatus.CANCELLED);
                            bookingService.deleteBooking(booking.getId());
                        }
                    }
                    tripService.updateTripStatus(trip.getId(), TripStatus.CANCELLED);
                    tripService.deleteTrip(trip.getId());
                }
            }
            Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
            vehicle.setDeleted(true);
            vehicleRepository.save(vehicle);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
}