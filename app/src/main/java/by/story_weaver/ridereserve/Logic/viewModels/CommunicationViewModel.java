package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.CommunicationLog;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.CommunicationLogRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class CommunicationViewModel extends ViewModel {
    private final CommunicationLogRepository commRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<List<CommunicationLog>>> logsState = new MutableLiveData<>();

    @Inject
    public CommunicationViewModel(CommunicationLogRepository commRepo, Executor executor) {
        this.commRepo = commRepo;
        this.executor = executor;
    }

    public LiveData<UiState<List<CommunicationLog>>> getLogsState() { return logsState; }

    public void loadLogsForBooking(final int bookingId) {
        logsState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<CommunicationLog> list = commRepo.getLogsByBooking(bookingId);
                logsState.postValue(UiState.success(list));
            } catch (Exception e) {
                logsState.postValue(UiState.error(e.getMessage()));
            }
        });
    }
}

