package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.BookingRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.RouteRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TripRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.UserRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class AdminViewModel extends ViewModel {
    private final RouteRepository routeRepo;
    private final TripRepository tripRepo;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<List<Route>>> routesState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Trip>>> tripsState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<User>>> usersState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Booking>>> bookingsState = new MutableLiveData<>();

    @Inject
    public AdminViewModel(RouteRepository routeRepo, TripRepository tripRepo,
                          UserRepository userRepo, BookingRepository bookingRepo, Executor executor) {
        this.routeRepo = routeRepo;
        this.tripRepo = tripRepo;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
        this.executor = executor;
    }

    public LiveData<UiState<List<Route>>> getRoutesState() { return routesState; }
    public LiveData<UiState<List<Trip>>> getTripsState() { return tripsState; }
    public LiveData<UiState<List<User>>> getUsersState() { return usersState; }
    public LiveData<UiState<List<Booking>>> getBookingsState() { return bookingsState; }

    public void loadAllData() {
        routesState.postValue(UiState.loading());
        tripsState.postValue(UiState.loading());
        usersState.postValue(UiState.loading());
        bookingsState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                routesState.postValue(UiState.success(routeRepo.getAllRoutes()));
                tripsState.postValue(UiState.success(tripRepo.getAll()));
                usersState.postValue(UiState.success(userRepo.getAll()));
                bookingsState.postValue(UiState.success(bookingRepo.getAll()));
            } catch (Exception e) {
                String msg = "Ошибка загрузки админ-данных: " + e.getMessage();
                routesState.postValue(UiState.error(msg));
                tripsState.postValue(UiState.error(msg));
                usersState.postValue(UiState.error(msg));
                bookingsState.postValue(UiState.error(msg));
            }
        });
    }

    public void deleteUser(final int id) {
        executor.execute(() -> {
            try {
                userRepo.removeUser(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

