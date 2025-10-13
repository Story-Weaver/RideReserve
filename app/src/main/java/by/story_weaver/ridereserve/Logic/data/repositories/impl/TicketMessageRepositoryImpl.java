package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.TicketMessageDao;
import by.story_weaver.ridereserve.Logic.data.models.TicketMessage;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.TicketMessageRepository;

@Singleton
public class TicketMessageRepositoryImpl implements TicketMessageRepository {
    private final TicketMessageDao dao;

    @Inject
    public TicketMessageRepositoryImpl(TicketMessageDao dao) { this.dao = dao; }

    @Override
    public void addMessage(TicketMessage message) { dao.addMessage(message); }

    @Override
    public List<TicketMessage> getMessagesByTicket(int ticketId) { return dao.getMessagesByTicket(ticketId); }

    @Override
    public void removeMessage(int id) { dao.removeMessage(id); }
}

