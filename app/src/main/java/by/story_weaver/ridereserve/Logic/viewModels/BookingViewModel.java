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
import by.story_weaver.ridereserve.Logic.data.models.User;
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

    private MutableLiveData<List<Route>> routes = new MutableLiveData<>();
    private MutableLiveData<List<Trip>> trips = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public BookingViewModel(BookingRepository bookingRepo, TripRepository tripRepo, RouteRepository routeRepo, VehicleRepository vehicleRepo) {
        this.bookingRepo = bookingRepo;
        this.tripRepo = tripRepo;
        this.routeRepo = routeRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public LiveData<List<Route>> getRoutes() { return routes; }
    public LiveData<List<Trip>> getTrips() { return trips; }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
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
    public void loadAllRoutes() {
        isLoading.setValue(true);
        routes.postValue(routeRepo.getAllRoutes());
        isLoading.postValue(false);
    }
    public void searchRoutesByNumber(String number) {
        isLoading.setValue(true);
        routes.postValue(routeRepo.getRoutesByNumber(number));
        isLoading.postValue(false);
    }
    public void searchRoutesByPoints(String from, String to) {
        isLoading.setValue(true);
        routes.postValue(routeRepo.geetRoutesByPoints(from, to));
        isLoading.postValue(false);
    }

    public void loadTripsForRoute(long routeId) {
        trips.postValue(tripRepo.getTripsByRoute(routeId));
        isLoading.postValue(false);
    }
    public boolean hasBookingForUserAndTrip(long passengerId, long tripId) {
        // Синхронная проверка - будьте осторожны, не вызывайте в UI потоке
        try {
            return bookingRepo.hasBookingForUserAndTrip(passengerId, tripId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

