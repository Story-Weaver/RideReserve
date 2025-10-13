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
public class BookingViewModel extends ViewModel {
    private final BookingRepository bookingRepo;
    private final TripRepository tripRepo;
    private final SeatRepository seatRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<List<Seat>>> availableSeats = new MutableLiveData<>();
    private final MutableLiveData<UiState<Booking>> bookingState = new MutableLiveData<>();

    @Inject
    public BookingViewModel(BookingRepository bookingRepo, TripRepository tripRepo, SeatRepository seatRepo, Executor executor) {
        this.bookingRepo = bookingRepo;
        this.tripRepo = tripRepo;
        this.seatRepo = seatRepo;
        this.executor = executor;
    }

    public LiveData<UiState<List<Seat>>> getAvailableSeats() { return availableSeats; }
    public LiveData<UiState<Booking>> getBookingState() { return bookingState; }

    public void loadAvailableSeats(final int tripId) {
        availableSeats.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                Trip t = tripRepo.getTrip(tripId);
                List<Seat> seats = seatRepo.getSeatsByVehicle(t.getVehicleId());
                // можно фильтровать занятые по bookingRepo.getBookingsByTrip(tripId)
                availableSeats.postValue(UiState.success(seats));
            } catch (Exception e) {
                availableSeats.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void bookSeat(final Booking booking) {
        bookingState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                // рекомендуется транзакция на уровне репозитория для проверки гонок
                bookingRepo.addBooking(booking);
                bookingState.postValue(UiState.success(booking));
            } catch (Exception e) {
                bookingState.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void cancelBooking(final int bookingId) {
        executor.execute(() -> {
            try {
                bookingRepo.removeBooking(bookingId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

