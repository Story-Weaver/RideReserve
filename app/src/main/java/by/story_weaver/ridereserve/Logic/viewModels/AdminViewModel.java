package by.story_weaver.ridereserve.Logic.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.*;
import by.story_weaver.ridereserve.Logic.network.AdminApiService;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AdminViewModel extends ViewModel {

    private final AdminApiService adminApiService;

    private final MutableLiveData<UiState<AdminStats>> adminStats = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Trip>>> activeTrips = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<User>>> users = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Route>>> routes = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Trip>>> trips = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Vehicle>>> vehicles = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Booking>>> bookings = new MutableLiveData<>();

    private final MutableLiveData<UiState<User>> userOperation = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> userDelete = new MutableLiveData<>();
    private final MutableLiveData<UiState<Route>> routeOperation = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> routeDelete = new MutableLiveData<>();
    private final MutableLiveData<UiState<Trip>> tripOperation = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> tripDelete = new MutableLiveData<>();
    private final MutableLiveData<UiState<Vehicle>> vehicleOperation = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> vehicleDelete = new MutableLiveData<>();
    private final MutableLiveData<UiState<Booking>> bookingOperation = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> bookingDelete = new MutableLiveData<>();

    @Inject
    public AdminViewModel(AdminApiService adminApiService) {
        this.adminApiService = adminApiService;
    }

    public LiveData<UiState<AdminStats>> getAdminStats() { return adminStats; }
    public LiveData<UiState<List<Trip>>> getActiveTrips() { return activeTrips; }
    public LiveData<UiState<List<User>>> getUsers() { return users; }
    public LiveData<UiState<List<Route>>> getRoutes() { return routes; }
    public LiveData<UiState<List<Trip>>> getTrips() { return trips; }
    public LiveData<UiState<List<Vehicle>>> getVehicles() { return vehicles; }
    public LiveData<UiState<List<Booking>>> getBookings() { return bookings; }

    public LiveData<UiState<User>> getUserOperation() { return userOperation; }
    public LiveData<UiState<Route>> getRouteOperation() { return routeOperation; }
    public LiveData<UiState<Trip>> getTripOperation() { return tripOperation; }
    public LiveData<UiState<Vehicle>> getVehicleOperation() { return vehicleOperation; }
    //public LiveData<UiState<Booking>> getBookingOperation() { return bookingOperation; }

    public void loadAdminData() {
        adminStats.postValue(UiState.loading());
        activeTrips.postValue(UiState.loading());

        adminApiService.getAdminStats().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AdminStats> call, Response<AdminStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adminStats.postValue(UiState.success(response.body()));
                } else {
                    adminStats.postValue(UiState.error("Ошибка загрузки статистики: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<AdminStats> call, Throwable t) {
                adminStats.postValue(UiState.error(t.getMessage()));
            }
        });

        adminApiService.getActiveTrips().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeTrips.postValue(UiState.success(response.body()));
                } else {
                    activeTrips.postValue(UiState.error("Ошибка загрузки активных поездок: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                activeTrips.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadAllUsers() {
        users.postValue(UiState.loading());
        adminApiService.getAllUsers().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users.postValue(UiState.success(response.body()));
                } else {
                    users.postValue(UiState.error("Ошибка загрузки пользователей: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                users.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadAllRoutes() {
        routes.postValue(UiState.loading());
        adminApiService.getAllRoutes().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                if (response.isSuccessful()) {
                    routes.postValue(UiState.success(response.body()));
                } else {
                    routes.postValue(UiState.error("Ошибка загрузки маршрутов: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                routes.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadAllTrips() {
        trips.postValue(UiState.loading());
        adminApiService.getAllTrips().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                if (response.isSuccessful()) {
                    trips.postValue(UiState.success(response.body()));
                } else {
                    trips.postValue(UiState.error("Ошибка загрузки поездок: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                trips.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadAllVehicles() {
        vehicles.postValue(UiState.loading());
        adminApiService.getAllVehicles().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                if (response.isSuccessful()) {
                    vehicles.postValue(UiState.success(response.body()));
                } else {
                    vehicles.postValue(UiState.error("Ошибка загрузки транспорта: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                vehicles.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadAllBookings() {
        bookings.postValue(UiState.loading());
        adminApiService.getAllBookings().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful()) {
                    bookings.postValue(UiState.success(response.body()));
                } else {
                    bookings.postValue(UiState.error("Ошибка загрузки бронирований: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                bookings.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void createUser(User user) {
        userOperation.postValue(UiState.loading());
        adminApiService.createUser(user).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userOperation.postValue(UiState.success(response.body()));
                    loadAllUsers();
                } else {
                    userOperation.postValue(UiState.error("Ошибка создания пользователя: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void updateUser(User user) {
        userOperation.postValue(UiState.loading());
        adminApiService.updateUser(user.getId(), user).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userOperation.postValue(UiState.success(response.body()));
                    loadAllUsers(); // Обновляем список
                } else {
                    userOperation.postValue(UiState.error("Ошибка обновления пользователя: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void deleteUser(long id) {
        userOperation.postValue(UiState.loading());
        adminApiService.deleteUser(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    userOperation.postValue(UiState.success(null));
                    loadAllUsers();
                } else {
                    userOperation.postValue(UiState.error("Ошибка удаления пользователя: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                userOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void createRoute(Route route) {
        routeOperation.postValue(UiState.loading());
        adminApiService.createRoute(route).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Route> call, Response<Route> response) {
                if (response.isSuccessful() && response.body() != null) {
                    routeOperation.postValue(UiState.success(response.body()));
                    loadAllRoutes();
                } else {
                    routeOperation.postValue(UiState.error("Ошибка создания маршрута: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Route> call, Throwable t) {
                routeOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void updateRoute(Route route) {
        routeOperation.postValue(UiState.loading());
        adminApiService.updateRoute(route.getId(), route).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Route> call, Response<Route> response) {
                if (response.isSuccessful() && response.body() != null) {
                    routeOperation.postValue(UiState.success(response.body()));
                    loadAllRoutes();
                } else {
                    routeOperation.postValue(UiState.error("Ошибка обновления маршрута: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Route> call, Throwable t) {
                routeOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void deleteRoute(long id) {
        routeOperation.postValue(UiState.loading());
        adminApiService.deleteRoute(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    routeOperation.postValue(UiState.success(null));
                    loadAllRoutes();
                } else {
                    routeOperation.postValue(UiState.error("Ошибка удаления маршрута: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                routeOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void createTrip(Trip trip) {
        tripOperation.postValue(UiState.loading());
        adminApiService.createTrip(trip).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tripOperation.postValue(UiState.success(response.body()));
                    loadAllTrips();
                } else {
                    tripOperation.postValue(UiState.error("Ошибка создания поездки: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                tripOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void updateTrip(Trip trip) {
        tripOperation.postValue(UiState.loading());
        adminApiService.updateTrip(trip.getId(), trip).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tripOperation.postValue(UiState.success(response.body()));
                    loadAllTrips();
                } else {
                    tripOperation.postValue(UiState.error("Ошибка обновления поездки: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                tripOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void deleteTrip(long id) {
        tripOperation.postValue(UiState.loading());
        adminApiService.deleteTrip(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    tripOperation.postValue(UiState.success(null));
                    loadAllTrips();
                } else {
                    tripOperation.postValue(UiState.error("Ошибка удаления поездки: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                tripOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void createVehicle(Vehicle vehicle) {
        vehicleOperation.postValue(UiState.loading());
        adminApiService.createVehicle(vehicle).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicleOperation.postValue(UiState.success(response.body()));
                    loadAllVehicles();
                } else {
                    vehicleOperation.postValue(UiState.error("Ошибка создания транспорта: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                vehicleOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void updateVehicle(Vehicle vehicle) {
        vehicleOperation.postValue(UiState.loading());
        adminApiService.updateVehicle(vehicle.getId(), vehicle).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicleOperation.postValue(UiState.success(response.body()));
                    loadAllVehicles();
                } else {
                    vehicleOperation.postValue(UiState.error("Ошибка обновления транспорта: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                vehicleOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void deleteVehicle(long id) {
        vehicleOperation.postValue(UiState.loading());
        adminApiService.deleteVehicle(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    vehicleOperation.postValue(UiState.success(null));
                    loadAllVehicles();
                } else {
                    vehicleOperation.postValue(UiState.error("Ошибка удаления транспорта: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                vehicleOperation.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
            }
        });
    }
    public void cancelDriverFutureTrips(long id){
        //TODO
    }
}