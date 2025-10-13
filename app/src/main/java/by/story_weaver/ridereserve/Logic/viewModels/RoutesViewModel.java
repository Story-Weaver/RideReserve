package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.RouteRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class RoutesViewModel extends ViewModel {
    private final RouteRepository routeRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<List<Route>>> routesState = new MutableLiveData<>();

    @Inject
    public RoutesViewModel(RouteRepository routeRepo, Executor executor) {
        this.routeRepo = routeRepo;
        this.executor = executor;
    }

    public LiveData<UiState<List<Route>>> getRoutesState() { return routesState; }

    public void loadRoutes() {
        routesState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<Route> list = routeRepo.getAllRoutes();
                routesState.postValue(UiState.success(list));
            } catch (Exception e) {
                routesState.postValue(UiState.error(e.getMessage()));
            }
        });
    }
}
