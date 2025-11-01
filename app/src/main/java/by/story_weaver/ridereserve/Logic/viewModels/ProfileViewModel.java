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
        // --- ЛОГИ ОТПРАВЛЯЕМОГО ПОЛЬЗОВАТЕЛЯ ---
        try {
            Log.d("UserDebug", "=== Sending updateUserProfile request ===");
            Log.d("UserDebug", "METHOD: PUT");
            Log.d("UserDebug", "URL   : http://192.168.0.85:8080/api/users/" + user.getId() + "/profile");
            Log.d("UserDebug", "BODY  : " + new com.google.gson.GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(user));
        } catch (Exception e) {
            Log.e("UserDebug", "Error serializing user for log", e);
        }

        profileState.postValue(UiState.loading());

        Call<User> call = userApiService.updateUserProfile(user.getId(), user);

        // --- ЛОГИ ЗАГОЛОВКОВ (если есть кастомные) ---
        try {
            Log.d("UserDebug", "HEADERS:");
            for (String name : call.request().headers().names()) {
                Log.d("UserDebug", "  " + name + ": " + call.request().header(name));
            }
        } catch (Exception ignored) {}

        call.enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("UserDebug", "=== updateUserProfile RESPONSE ===");
                Log.d("UserDebug", "Response code: " + response.code());

                // Заголовки ответа
                for (String name : response.headers().names()) {
                    Log.d("UserDebug", "  " + name + ": " + response.headers().get(name));
                }

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

                    int currentInSystemId = userRepo.getUserInSystem();
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
                    Log.e("UserDebug", "Request URL: " + call.request().url());
                    Log.e("UserDebug", "updateUserProfile failed: " + err);
                    profileState.postValue(UiState.error(err));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserDebug", "=== updateUserProfile FAILURE ===");
                Log.e("UserDebug", "URL: " + call.request().url());
                Log.e("UserDebug", "Error: " + (t != null ? t.getMessage() : "unknown"));
                profileState.postValue(UiState.error("Сетевая ошибка: " + (t != null ? t.getMessage() : "unknown")));
            }
        });
    }


}
