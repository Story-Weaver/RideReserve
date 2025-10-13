package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.CommunicationLogDao;
import by.story_weaver.ridereserve.Logic.data.models.CommunicationLog;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.CommunicationLogRepository;

@Singleton
public class CommunicationLogRepositoryImpl implements CommunicationLogRepository {
    private final CommunicationLogDao dao;

    @Inject
    public CommunicationLogRepositoryImpl(CommunicationLogDao dao) { this.dao = dao; }

    @Override
    public void addLog(CommunicationLog log) { dao.addLog(log); }

    @Override
    public List<CommunicationLog> getLogsByBooking(int bookingId) { return dao.getLogsByBooking(bookingId); }

    @Override
    public void removeLog(int id) { dao.removeLog(id); }
}

