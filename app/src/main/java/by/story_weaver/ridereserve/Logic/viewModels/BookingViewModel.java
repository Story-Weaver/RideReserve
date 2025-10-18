package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Seat;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.BookingRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.RouteRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.SeatRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TripRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.VehicleRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class BookingViewModel extends ViewModel {
    private final BookingRepository bookingRepo;
    private final TripRepository tripRepo;
    private final RouteRepository routeRepo;
    private final VehicleRepository vehicleRepo;

    private final MutableLiveData<UiState<Booking>> bookingState = new MutableLiveData<>();

    @Inject
    public BookingViewModel(BookingRepository bookingRepo, TripRepository tripRepo, RouteRepository routeRepo, VehicleRepository vehicleRepo) {
        this.bookingRepo = bookingRepo;
        this.tripRepo = tripRepo;
        this.routeRepo = routeRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public List<Booking> getBookinglist(){
        return bookingRepo.getAll();
    }

    public List<Trip> getTriplist(){
        return tripRepo.getAll();
    }

    public List<Route> getRoutelist(){
        return routeRepo.getAllRoutes();
    }
    public List<Vehicle> getVehiclelist(){
        return vehicleRepo.getAllVehicles();
    }
    public void addBooking(Booking booking){
        bookingRepo.addBooking(booking);
    }
    public void addTrip(Trip trip){
        tripRepo.addTrip(trip);
    }
    public void addRoute(Route route){
        routeRepo.addRoute(route);
    }
    public void addVehicle(Vehicle vehicle){
        vehicleRepo.addVehicle(vehicle);
    }
}

