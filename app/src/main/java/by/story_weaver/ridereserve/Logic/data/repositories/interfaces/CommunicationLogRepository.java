package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.CommunicationLog;

import java.util.List;

public interface CommunicationLogRepository {
    void addLog(CommunicationLog log);
    List<CommunicationLog> getLogsByBooking(int bookingId);
    void removeLog(int id);
}
