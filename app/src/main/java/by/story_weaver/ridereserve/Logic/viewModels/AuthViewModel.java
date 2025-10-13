package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.UserRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final UserRepository userRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<User>> currentUserState = new MutableLiveData<>();

    @Inject
    public AuthViewModel(UserRepository userRepo, Executor executor) {
        this.userRepo = userRepo;
        this.executor = executor;
    }

    public LiveData<UiState<User>> getCurrentUserState() { return currentUserState; }

    public void checkSignedIn() {
        currentUserState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                int id = 1; //userRepo.getIdUserInSystem();
                //TODO
                if (id > 0) {
                    User u = userRepo.getUser(id);
                    currentUserState.postValue(UiState.success(u));
                } else {
                    currentUserState.postValue(UiState.success(null));
                }
            } catch (Exception e) {
                currentUserState.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void login(final String email, final String password) {
        currentUserState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                //todo
                User u = null; //userRepo.getUserByEmail(email);
                if (u != null && u.getPassword().equals(password)) {
                    //userRepo.setUserInSystem(u.getId());
                    currentUserState.postValue(UiState.success(u));
                } else {
                    currentUserState.postValue(UiState.error("Неверные учетные данные"));
                }
            } catch (Exception e) {
                currentUserState.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void logout() {
        executor.execute(() -> {
            try {
                //todo
                currentUserState.postValue(UiState.success(null));
            } catch (Exception e) {
            }
        });
    }

    public void register(final User user) {
        currentUserState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                userRepo.addUser(user);
                currentUserState.postValue(UiState.success(user));
            } catch (Exception e) {
                currentUserState.postValue(UiState.error(e.getMessage()));
            }
        });
    }
}