package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.EnterRequest;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.UserRepository;
import by.story_weaver.ridereserve.Logic.network.AuthApiService;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final UserRepository userRepo;
    private final AuthApiService authApiService;
    private final MutableLiveData<UiState<User>> UserStateReg = new MutableLiveData<>();
    private final MutableLiveData<UiState<User>> UserStateEnter = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> logOut = new MutableLiveData<>();

    @Inject
    public AuthViewModel(UserRepository userRepo, AuthApiService authApiService) {
        this.userRepo = userRepo;
        this.authApiService = authApiService;
    }

    public LiveData<UiState<User>> getUserStateEnter() { return UserStateEnter; }
    public LiveData<UiState<User>> getUserStateReg() { return UserStateReg; }
    public LiveData<UiState<Boolean>> getLogOut() { return logOut; }

    public long checkSignedIn() {
        long id = userRepo.getUserInSystem();
        if (id > 0) {
            return id;
        } else {
            return -1;
        }
    }
    public void setUserInSystem(long id){
        try {
            userRepo.setUserInSystem(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void login(final String email, final String password) {
        UserStateEnter.postValue(UiState.loading());
        authApiService.login(new EnterRequest(email, password)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    userRepo.addUser(user);
                    userRepo.setUserInSystem(user.getId());
                    UserStateEnter.postValue(UiState.success(user));
                } else {
                    UserStateEnter.postValue(UiState.error("Ошибка авторизации"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                UserStateEnter.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void register(User user) {
        UserStateReg.postValue(UiState.loading());
        authApiService.register(user).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User newUser = response.body();
                    userRepo.addUser(newUser);
                    userRepo.setUserInSystem(newUser.getId());
                    UserStateReg.postValue(UiState.success(newUser));
                } else {
                    UserStateReg.postValue(UiState.error("Ошибка регистрации"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                UserStateReg.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    public void logout() {
        logOut.postValue(UiState.loading());
        if(userRepo.exit()){
            logOut.postValue(UiState.success(true));
        } else {
            logOut.postValue(UiState.error(""));
        }
    }
    public void addUser(User user){
        userRepo.addUser(user);
    }
}