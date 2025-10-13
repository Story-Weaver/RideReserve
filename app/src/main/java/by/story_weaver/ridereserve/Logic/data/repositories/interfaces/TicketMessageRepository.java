package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.TicketMessage;

import java.util.List;

public interface TicketMessageRepository {
    void addMessage(TicketMessage message);
    List<TicketMessage> getMessagesByTicket(int ticketId);
    void removeMessage(int id);
}

