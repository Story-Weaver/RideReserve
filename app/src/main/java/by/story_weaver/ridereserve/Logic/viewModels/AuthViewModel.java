package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.UserRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final UserRepository userRepo;
    private final Executor executor;
    private final MutableLiveData<UiState<User>> currentUserStateReg = new MutableLiveData<>();
    private final MutableLiveData<UiState<User>> currentUserStateEnter = new MutableLiveData<>();

    @Inject
    public AuthViewModel(UserRepository userRepo, Executor executor) {
        this.userRepo = userRepo;
        this.executor = executor;
    }

    public LiveData<UiState<User>> getCurrentUserStateEnter() { return currentUserStateEnter; }
    public LiveData<UiState<User>> getCurrentUserStateReg() { return currentUserStateReg; }

    public int checkSignedIn() {
        int id = userRepo.getUserInSystem();
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
        currentUserStateEnter.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                User user = userRepo.getUserByEmail(email);
                if (user.getPassword().equals(password) && !user.getRole().equals(UserRole.GUEST)){
                    currentUserStateEnter.postValue(UiState.success(user));
                } else currentUserStateEnter.postValue(UiState.error("Некорректные авторизационные данные"));
            } catch (Exception e) {
                currentUserStateEnter.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void logout() {
        userRepo.exit();
    }

    public void register(User user) {
        currentUserStateReg.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                User u = new User(1, user.getEmail(), user.getPassword(), user.getFullName(), user.getPhone(),1, UserRole.PASSENGER);
                userRepo.addUser(u);
                currentUserStateReg.postValue(UiState.success(user));
            } catch (Exception e) {
                currentUserStateReg.postValue(UiState.error(e.getMessage()));
            }
        });
    }
    public void addUser(User user){
        userRepo.addUser(user);
    }
}