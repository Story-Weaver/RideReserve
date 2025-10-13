package by.story_weaver.ridereserve.Logic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import by.story_weaver.ridereserve.Logic.data.models.SupportTicket;
import by.story_weaver.ridereserve.Logic.data.models.TicketMessage;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.SupportRepository;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TicketMessageRepository;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class SupportViewModel extends ViewModel {
    private final SupportRepository supportRepo;
    private final TicketMessageRepository msgRepo;
    private final Executor executor;

    private final MutableLiveData<UiState<List<SupportTicket>>> ticketsState = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<TicketMessage>>> messagesState = new MutableLiveData<>();

    @Inject
    public SupportViewModel(SupportRepository supportRepo, TicketMessageRepository msgRepo, Executor executor) {
        this.supportRepo = supportRepo;
        this.msgRepo = msgRepo;
        this.executor = executor;
    }

    public LiveData<UiState<List<SupportTicket>>> getTicketsState() { return ticketsState; }
    public LiveData<UiState<List<TicketMessage>>> getMessagesState() { return messagesState; }

    public void loadTicketsForUser(final int userId) {
        ticketsState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<SupportTicket> list = supportRepo.getTicketsByUser(userId);
                ticketsState.postValue(UiState.success(list));
            } catch (Exception e) {
                ticketsState.postValue(UiState.error(e.getMessage()));
            }
        });
    }

    public void createTicket(final SupportTicket ticket) {
        executor.execute(() -> {
            try {
                supportRepo.addTicket(ticket);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loadMessages(final int ticketId) {
        messagesState.postValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<TicketMessage> list = msgRepo.getMessagesByTicket(ticketId);
                messagesState.postValue(UiState.success(list));
            } catch (Exception e) {
                messagesState.postValue(UiState.error(e.getMessage()));
            }
        });
    }
}

