package by.story_weaver.ridereserve.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Booking;
import by.story_weaver.ridereserve.models.Route;
import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.models.enums.BookingStatus;
import by.story_weaver.ridereserve.models.enums.TripStatus;
import by.story_weaver.ridereserve.repositories.RouteRepository;
import by.story_weaver.ridereserve.services.BookingService;
import by.story_weaver.ridereserve.services.RouteService;
import by.story_weaver.ridereserve.services.TripService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RouteServiceImpl implements RouteService{

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TripService tripService;

    @Autowired
    private BookingService bookingService;

    @Override
    public List<Route> getAllRoutes() {
        try {
            List<Route> list = routeRepository.findAll();
            List<Route> finalList = new ArrayList<>();
            for (Route route : list) {
                if(!route.getDeleted()){
                    finalList.add(route);
                }
            }
            return finalList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Route getRouteById(long id) {
        try {
            return routeRepository.getRouteById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Route createRoute(Route route) {
        try {
            return routeRepository.save(route);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Route updateRoute(Route route) {
        try {
            return routeRepository.save(route);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteRoute(long id) {
        try {
            List<Trip> list = tripService.getTripsByRoute(id);
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
            Route route = routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Route not found"));
            route.setDeleted(true);
            routeRepository.save(route);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public List<String> getAllCities() {
        try {
            List<String> origins = routeRepository.findDistinctOrigins();
            List<String> destinations = routeRepository.findDistinctDestinations();
            Set<String> cities = new HashSet<>();
            if (origins != null) cities.addAll(origins);
            if (destinations != null) cities.addAll(destinations);
            List<String> sorted = new ArrayList<>(cities);
            sorted.sort(String::compareTo);
            return sorted;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    

    @Override
    public List<Route> searchRoutesByNumber(String text) {
        try {
            return routeRepository.findByNameContaining(text);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Route> searchRoutesByPoints(String from, String to) {
        try {
            return routeRepository.findByOriginAndDestination(from, to);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getRoutePoints() {
        return null;
        //TODO
    }
    
}
