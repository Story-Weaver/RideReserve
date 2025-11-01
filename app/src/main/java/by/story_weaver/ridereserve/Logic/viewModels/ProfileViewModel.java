package by.story_weaver.ridereserve.Logic.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.UserRepository;
import by.story_weaver.ridereserve.Logic.network.UserApiService;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final UserRepository userRepo;
    private final UserApiService userApiService;
    private final Executor executor;

    private final MutableLiveData<UiState<User>> profileState = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(UserRepository userRepo, Executor executor, UserApiService userApiService) {
        this.userRepo = userRepo;
        this.executor = executor;
        this.userApiService = userApiService;
    }

    public LiveData<UiState<User>> getProfileState() { return profileState; }

    public User getProfile() {
        userApiService.getUserById(userRepo.getUserInSystem()).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                user.setInSystem(1);
                userRepo.editUser(user);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        return userRepo.getUser(userRepo.getUserInSystem());
    }

    public boolean addGuest(User guest) {
        try {
            userRepo.addUser(guest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getIdByEmail(String email) {
        User user = userRepo.getUserByEmail(email);
        return user != null ? user.getId() : -1;
    }

    public User getUserById(long id){
        return userRepo.getUser(id);
    }

    public void editUser(User user) {
        if (user == null) {
            profileState.postValue(UiState.error("Пустой пользователь"));
            return;
        }
        user.setInSystem(0);

        profileState.postValue(UiState.loading());

        userApiService.updateUserProfile(user.getId(), user).enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                try {
                    String errorBody = response.errorBody() != null
                            ? response.errorBody().string()
                            : "";
                    if (!errorBody.isEmpty()) {
                        Log.d("UserDebug", "errorBody: " + errorBody);
                    }
                } catch (Exception ignored) {}

                if (response.isSuccessful() && response.body() != null) {
                    User updated = response.body();
                    Log.d("UserDebug", "SUCCESS BODY: " +
                            new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(updated));

                    executor.execute(() -> {
                        try {
                            User local = userRepo.getUser(updated.getId());
                            if (local != null) {
                                updated.setInSystem(local.getInSystem());
                            }
                            userRepo.updateUser(updated);
                        } catch (Exception e) {
                            Log.e("UserDebug", "Error saving user locally", e);
                        }
                    });

                    long currentInSystemId = userRepo.getUserInSystem();
                    if (currentInSystemId == updated.getId()) {
                        userRepo.setUserInSystem(updated.getId());
                    }

                    profileState.postValue(UiState.success(updated));
                } else {
                    String err = "Ошибка сервера: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            err += " - " + response.errorBody().string();
                        }
                    } catch (IOException ignored) {}
                    profileState.postValue(UiState.error(err));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                profileState.postValue(UiState.error("Сетевая ошибка: " + (t != null ? t.getMessage() : "unknown")));
            }
        });
    }


}
