// AdminViewModel.java
package by.story_weaver.ridereserve.Logic.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import by.story_weaver.ridereserve.Logic.data.models.AdminStats;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.network.impl.AdminApiService;
import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AdminViewModel extends ViewModel {

    private final AdminApiService adminApiService;
    private final MutableLiveData<AdminStats> adminStats = new MutableLiveData<>();
    private final MutableLiveData<List<Trip>> activeTrips = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public AdminViewModel(AdminApiService adminApiService) {
        this.adminApiService = adminApiService;
    }

    public LiveData<AdminStats> getAdminStats() {
        return adminStats;
    }

    public LiveData<List<Trip>> getActiveTrips() {
        return activeTrips;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadAdminData() {
        isLoading.setValue(true);
        Log.v("adminViewModel", "start_loading");
        // Загружаем статистику
        adminApiService.getAdminStats().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AdminStats> call, Response<AdminStats> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    adminStats.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<AdminStats> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("adminViewModel", "error: " + t.getMessage());
            }
        });

        adminApiService.getActiveTrips().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeTrips.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                Log.e("adminViewModel", "error: " + t.getMessage());
            }
        });
    }
}