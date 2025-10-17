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
public class ProfileViewModel extends ViewModel {
    private final UserRepository userRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<User>> profileState = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(UserRepository userRepo, Executor executor) {
        this.userRepo = userRepo;
        this.executor = executor;
    }

    public LiveData<UiState<User>> getProfileState() { return profileState; }

    public User getProfile() {
        return userRepo.getUser(userRepo.getUserInSystem());
    }

    public void updateProfile(User user) {
        profileState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                //todo
                profileState.postValue(UiState.success(user));
            } catch (Exception e) {
                profileState.postValue(UiState.error(e.getMessage()));
            }
        });
    }
}
