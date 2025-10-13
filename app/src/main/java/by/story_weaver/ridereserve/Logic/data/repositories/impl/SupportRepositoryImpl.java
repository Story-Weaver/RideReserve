package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.SupportDao;
import by.story_weaver.ridereserve.Logic.data.models.SupportTicket;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.SupportRepository;

@Singleton
public class SupportRepositoryImpl implements SupportRepository {
    private final SupportDao dao;

    @Inject
    public SupportRepositoryImpl(SupportDao dao) { this.dao = dao; }

    @Override
    public void addTicket(SupportTicket ticket) { dao.addTicket(ticket); }

    @Override
    public void removeTicket(int id) { dao.removeTicket(id); }

    @Override
    public SupportTicket getTicket(int id) { return dao.getTicket(id); }

    @Override
    public List<SupportTicket> getTicketsByUser(int userId) { return dao.getTicketsByUser(userId); }
}

