package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TripRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class TripsViewModel extends ViewModel {
    private final TripRepository tripRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<List<Trip>>> tripsState = new MutableLiveData<>();

    @Inject
    public TripsViewModel(TripRepository tripRepo, Executor executor) {
        this.tripRepo = tripRepo;
        this.executor = executor;
    }

    public LiveData<UiState<List<Trip>>> getTripsState() { return tripsState; }

    public void loadTripsByRoute(final int routeId) {
        tripsState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<Trip> list = tripRepo.getTripsByRoute(routeId);
                tripsState.postValue(UiState.success(list));
            } catch (Exception e) {
                tripsState.postValue(UiState.error(e.getMessage()));
            }
        });
    }
}
