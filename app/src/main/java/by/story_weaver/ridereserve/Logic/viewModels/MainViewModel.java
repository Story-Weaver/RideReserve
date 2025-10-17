package by.story_weaver.ridereserve.Logic.viewModels;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final MutableLiveData<Fragment> openRequest = new MutableLiveData<>();
    private final MutableLiveData<Boolean> closeRequest = new MutableLiveData<>();
    @Inject
    public MainViewModel() {

    }
    public void openFullscreen(Fragment f) {
        openRequest.postValue(f);
    }
    public void closeFullscreen() {
        closeRequest.postValue(true);
    }
    public LiveData<Fragment> openRequest() {
        return openRequest;
    }
    public LiveData<Boolean> closeRequest() {
        return closeRequest;
    }

}

