package by.story_weaver.ridereserve.Logic.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import by.story_weaver.ridereserve.Logic.data.models.AdminStats;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
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

    @Inject
    public AdminViewModel(AdminApiService adminApiService) {
        this.adminApiService = adminApiService;
    }

    public LiveData<UiState<AdminStats>> getAdminStats() {
        return adminStats;
    }

    public LiveData<UiState<List<Trip>>> getActiveTrips() {
        return activeTrips;
    }


    public void loadAdminData() {
        adminStats.postValue(UiState.loading());
        activeTrips.postValue(UiState.loading());
        Log.v("adminViewModel", "start_loading");
        adminApiService.getAdminStats().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AdminStats> call, Response<AdminStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adminStats.postValue(UiState.success(response.body()));
                }
            }

            @Override
            public void onFailure(Call<AdminStats> call, Throwable t) {
                adminStats.postValue(UiState.error(t.getMessage()));
                Log.e("adminViewModel", "error: " + t.getMessage());
            }
        });

        adminApiService.getActiveTrips().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeTrips.postValue(UiState.success(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                activeTrips.postValue(UiState.error(t.getMessage()));
                Log.e("adminViewModel", "error: " + t.getMessage());
            }
        });
    }
}