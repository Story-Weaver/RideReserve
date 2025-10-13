package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Seat;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.BookingRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.SeatRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TripRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class TripDetailViewModel extends ViewModel {
    private final TripRepository tripRepo;
    private final SeatRepository seatRepo;
    private final BookingRepository bookingRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<Trip>> tripState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Seat>>> seatsState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Booking>>> bookingsState = new MutableLiveData<>();

    @Inject
    public TripDetailViewModel(TripRepository tripRepo, SeatRepository seatRepo, BookingRepository bookingRepo, Executor executor) {
        this.tripRepo = tripRepo;
        this.seatRepo = seatRepo;
        this.bookingRepo = bookingRepo;
        this.executor = executor;
    }

    public LiveData<UiState<Trip>> getTripState() { return tripState; }
    public LiveData<UiState<List<Seat>>> getSeatsState() { return seatsState; }
    public LiveData<UiState<List<Booking>>> getBookingsState() { return bookingsState; }

    public void loadTripDetail(final int tripId) {
        tripState.postValue(UiState.loading());
        seatsState.postValue(UiState.loading());
        bookingsState.postValue(UiState.loading());

        executor.execute(() -> {
            try {
                Trip t = tripRepo.getTrip(tripId);
                tripState.postValue(UiState.success(t));

                List<Seat> seats = seatRepo.getSeatsByVehicle(t.getVehicleId());
                seatsState.postValue(UiState.success(seats));

                List<Booking> bookings = bookingRepo.getBookingsByTrip(tripId);
                bookingsState.postValue(UiState.success(bookings));
            } catch (Exception e) {
                tripState.postValue(UiState.error(e.getMessage()));
                seatsState.postValue(UiState.error(e.getMessage()));
                bookingsState.postValue(UiState.error(e.getMessage()));
            }
        });
    }
}
