package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.CommunicationLog;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.BookingRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.CommunicationLogRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TripRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.UserRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class DriverViewModel extends ViewModel {
    private final TripRepository tripRepo;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final CommunicationLogRepository commRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<List<Trip>>> myTrips = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Booking>>> passengers = new MutableLiveData<>();

    @Inject
    public DriverViewModel(TripRepository tripRepo, BookingRepository bookingRepo,
                           UserRepository userRepo, CommunicationLogRepository commRepo, Executor executor) {
        this.tripRepo = tripRepo;
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.commRepo = commRepo;
        this.executor = executor;
    }

    public LiveData<UiState<List<Trip>>> getMyTrips() { return myTrips; }
    public LiveData<UiState<List<Booking>>> getPassengers() { return passengers; }

    public void loadTripsForDriver(final int driverId) {
        myTrips.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<Trip> list = tripRepo.getTripsByDriver(driverId);
                myTrips.postValue(UiState.success(list));
            } catch (Exception e) {
                myTrips.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void loadPassengersForTrip(final int tripId) {
        passengers.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<Booking> list = bookingRepo.getBookingsByTrip(tripId);
                passengers.postValue(UiState.success(list));
            } catch (Exception e) {
                passengers.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void contactPassenger(final int fromUserId, final int toUserId, final int bookingId, final String method, final String notes) {
        executor.execute(() -> {
            try {
                CommunicationLog log = new CommunicationLog(0, bookingId, fromUserId, toUserId, method, String.valueOf(System.currentTimeMillis()), notes);
                commRepo.addLog(log);
            } catch (Exception e) {
            }
        });
    }
}

