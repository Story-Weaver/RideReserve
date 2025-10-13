package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.SupportTicket;

import java.util.List;

public interface SupportRepository {
    void addTicket(SupportTicket ticket);
    void removeTicket(int id);
    SupportTicket getTicket(int id);
    List<SupportTicket> getTicketsByUser(int userId);
}

