package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.Seat;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.SeatRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.VehicleRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class VehiclesViewModel extends ViewModel {
    private final VehicleRepository vehicleRepo;
    private final SeatRepository seatRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<List<Vehicle>>> vehiclesState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Seat>>> seatsState = new MutableLiveData<>();

    @Inject
    public VehiclesViewModel(VehicleRepository vehicleRepo, SeatRepository seatRepo, Executor executor) {
        this.vehicleRepo = vehicleRepo;
        this.seatRepo = seatRepo;
        this.executor = executor;
    }

    public LiveData<UiState<List<Vehicle>>> getVehiclesState() { return vehiclesState; }
    public LiveData<UiState<List<Seat>>> getSeatsState() { return seatsState; }

    public void loadVehicles() {
        vehiclesState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<Vehicle> list = vehicleRepo.getAllVehicles();
                vehiclesState.postValue(UiState.success(list));
            } catch (Exception e) {
                vehiclesState.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void loadSeatsForVehicle(final int vehicleId) {
        seatsState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<Seat> list = seatRepo.getSeatsByVehicle(vehicleId);
                seatsState.postValue(UiState.success(list));
            } catch (Exception e) {
                seatsState.postValue(UiState.error(e.getMessage()));
            }
        });
    }
}
