package by.story_weaver.ridereserve.Logic.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.enums.*;
import by.story_weaver.ridereserve.Logic.data.models.*;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.*;
import by.story_weaver.ridereserve.Logic.network.*;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class BookingViewModel extends ViewModel {
    private final TripApiService tripApiService;
    private final DriverApiService driverApiService;
    private final RouteApiService routeApiService;
    private final BookingApiService bookingApiService;
    private final VehicleApiService vehicleApiService;
    private final UserApiService userApiService;
    private final UserRepository userRepository;

    private final MutableLiveData<UiState<List<Route>>> allRoutes = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<String>>> allCities = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Route>>> filteredRoutes = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Trip>>> allTrips = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Trip>>> tripsForRoute = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Trip>>> driverTrips = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> isHasBooking = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Booking>>> bookingsForUser = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Booking>>> allBookings = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Booking>>> bookingsForTrip = new MutableLiveData<>();
    private final MutableLiveData<UiState<Booking>> bookingStatusChanged = new MutableLiveData<>();
    private final MutableLiveData<UiState<Trip>> tripStatusChanged = new MutableLiveData<>();
    private final MutableLiveData<UiState<Trip>> tripById = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> bookingCreated = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Vehicle>>> allVehicles = new MutableLiveData<>();
    private final MutableLiveData<UiState<Vehicle>> vehicleById = new MutableLiveData<>();
    private final MutableLiveData<UiState<User>> userById = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<User>>> passengers = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Booking>>> filteredBookings = new MutableLiveData<>();

    @Inject
    public BookingViewModel(TripApiService tripApiService, BookingApiService bookingApiService,
                            UserRepository userRepository, RouteApiService routeApiService,
                            VehicleApiService vehicleApiService, UserApiService userApiService,
                            DriverApiService driverApiService) {
        this.tripApiService = tripApiService;
        this.driverApiService = driverApiService;
        this.bookingApiService = bookingApiService;
        this.userRepository = userRepository;
        this.routeApiService = routeApiService;
        this.vehicleApiService = vehicleApiService;
        this.userApiService = userApiService;
    }

    // Getters for LiveData
    public LiveData<UiState<List<User>>> getPassengers(){
        return passengers;
    }
    public LiveData<UiState<List<String>>> getAllCitiesLive() { return allCities; }
    public LiveData<UiState<List<Route>>> getAllRoutes() { return allRoutes; }
    public LiveData<UiState<List<Route>>> getFilteredRoutes() { return filteredRoutes; }
    public LiveData<UiState<List<Trip>>> getAllTrips() { return allTrips; }
    public LiveData<UiState<List<Trip>>> getTripsForRoute() { return tripsForRoute; }
    public LiveData<UiState<List<Trip>>> getDriverTrips() { return driverTrips; }
    public LiveData<UiState<Boolean>> getIsHasBooking() { return isHasBooking; }
    public LiveData<UiState<List<Booking>>> getBookingsForUser() { return bookingsForUser; }
    public LiveData<UiState<List<Booking>>> getAllBookings() { return allBookings; }
    public LiveData<UiState<List<Booking>>> getBookingsForTrip() { return bookingsForTrip; }
    public LiveData<UiState<Booking>> getBookingStatusChanged() { return bookingStatusChanged; }
    public LiveData<UiState<Trip>> getTripStatusChanged() { return tripStatusChanged; }
    public LiveData<UiState<Trip>> getTripById() { return tripById; }
    public LiveData<UiState<Boolean>> getBookingCreated() { return bookingCreated; }
    public LiveData<UiState<List<Vehicle>>> getAllVehicles() { return allVehicles; }
    public LiveData<UiState<Vehicle>> getVehicleById() { return vehicleById; }
    public LiveData<UiState<User>> getUserById() { return userById; }

    // Route methods
    public void loadAllCities() {
        allCities.postValue(UiState.loading());
        routeApiService.getCities().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    allCities.postValue(UiState.success(response.body()));
                } else {
                    String err = "Error code: " + response.code();
                    try {
                        if (response.errorBody() != null) err += " - " + response.errorBody().string();
                    } catch (IOException ignored) {}
                    allCities.postValue(UiState.error(err));
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                allCities.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadAllRoutes() {
        allRoutes.postValue(UiState.loading());
        routeApiService.getAllRoutes().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Route>> call, @NonNull Response<List<Route>> response) {
                allRoutes.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<List<Route>> call, @NonNull Throwable t) {
                allRoutes.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadPassengers(long tripId){
        passengers.postValue(UiState.loading());
        userApiService.getPassengers(tripId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                passengers.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                passengers.postValue(UiState.error(t.getMessage()));
            }
        });

    }
    public void loadRoutesByNumber(String number) {
        filteredRoutes.postValue(UiState.loading());
        routeApiService.searchRoutesByNumber(number).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                filteredRoutes.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                filteredRoutes.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadRoutesByPoints(String from, String to) {
        filteredRoutes.postValue(UiState.loading());
        routeApiService.searchRoutesByPoints(from, to).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                filteredRoutes.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                filteredRoutes.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    // Trip methods
    public void loadAllTrips() {
        allTrips.postValue(UiState.loading());
        tripApiService.getAllTrips().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Trip>> call, @NonNull Response<List<Trip>> response) {
                allTrips.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<List<Trip>> call, @NonNull Throwable t) {
                allTrips.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void loadTripsForRoute(long routeId) {
        tripsForRoute.postValue(UiState.loading());
        tripApiService.getTripsByRoute(routeId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                tripsForRoute.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                tripsForRoute.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void loadDriverTrips(long driverId) {
        driverTrips.postValue(UiState.loading());
        Call<List<Trip>> call = driverApiService.getDriverTrips(driverId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                Log.v("response", "" + response.body());
                driverTrips.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                driverTrips.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void loadTripById(long tripId) {
        tripById.postValue(UiState.loading());
        tripApiService.getTripById(tripId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                tripById.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                tripById.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void changeStatusTrip(long tripId, TripStatus status) {
        tripStatusChanged.postValue(UiState.loading());
        tripApiService.updateTripStatus(tripId, status).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                tripStatusChanged.postValue(UiState.success(response.body()));
                // Refresh trips data after status change
                loadAllTrips();
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                tripStatusChanged.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    // Booking methods
    public void hasBookingForUserAndTrip(long passengerId, long tripId) {
        isHasBooking.postValue(UiState.loading());
        bookingApiService.checkBookingExists(passengerId, tripId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                isHasBooking.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                isHasBooking.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void loadBookingsForUser() {
        bookingsForUser.postValue(UiState.loading());
        long userId = userRepository.getUserInSystem();
        bookingApiService.getBookingsByUser(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                bookingsForUser.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                bookingsForUser.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void loadAllBookings() {
        allBookings.postValue(UiState.loading());
        bookingApiService.getAllBookings().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                allBookings.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                allBookings.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void loadBookingsForTrip(long tripId) {
        bookingsForTrip.postValue(UiState.loading());
        bookingApiService.getBookingsByTrip(tripId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                bookingsForTrip.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                bookingsForTrip.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void changeStatusBooking(long bookingId, BookingStatus status) {
        bookingStatusChanged.postValue(UiState.loading());
        bookingApiService.updateBookingStatus(bookingId, status.name()).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                try {
                    if (response.errorBody() != null) {
                        Log.d("BookingDebug", "updateStatus errorBody: " + response.errorBody().string());
                    }
                } catch (IOException ex) {
                    Log.e("BookingDebug", "Error reading errorBody", ex);
                }

                if (response.isSuccessful() && response.body() != null) {
                    bookingStatusChanged.postValue(UiState.success(response.body()));
                    loadBookingsForUser();
                    loadAllBookings();
                } else {
                    bookingStatusChanged.postValue(UiState.error("Failed to update booking: code=" + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                bookingStatusChanged.postValue(UiState.error("Network error: " + t.getMessage()));
            }
        });
    }


    public void createBooking(Booking booking) {
        bookingCreated.postValue(UiState.loading());
        bookingApiService.createBooking(booking).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if(response.isSuccessful() && response.body() != null){
                    bookingCreated.postValue(UiState.success(true));
                    loadBookingsForUser();
                } else {
                    String errorMsg = "Failed to create booking. Code: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " - " + response.errorBody().string();
                        } catch (IOException e) {
                            errorMsg += " - Error reading error body";
                        }
                    }
                    bookingCreated.postValue(UiState.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                bookingCreated.postValue(UiState.error("Network error: " + t.getMessage()));
            }
        });
    }

    // Vehicle methods
    public void loadAllVehicles() {
        allVehicles.postValue(UiState.loading());
        vehicleApiService.getAllVehicles().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                allVehicles.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                allVehicles.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void loadVehicleById(long vehicleId) {
        vehicleById.postValue(UiState.loading());
        vehicleApiService.getVehicleById(vehicleId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                vehicleById.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                vehicleById.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    // User methods
    public void loadUserById(long userId) {
        userById.postValue(UiState.loading());
        userApiService.getUserById(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                userById.postValue(UiState.success(response.body()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userById.postValue(UiState.error(t.getMessage()));
            }
        });
    }
}