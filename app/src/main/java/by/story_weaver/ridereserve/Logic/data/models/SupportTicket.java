package by.story_weaver.ridereserve.Logic.data.models;

import by.story_weaver.ridereserve.Logic.data.enums.TicketStatus;
import by.story_weaver.ridereserve.Logic.data.enums.TicketType;

public class SupportTicket {
    private long id;
    private long userId;
    private TicketType type;
    private TicketStatus status;
    private String subject;
    private String message;
    private String createdAt;

    public SupportTicket(int anInt, int cursorInt, String string, String cursorString, String subject, String message, String createdAt) {}
    public SupportTicket(long id, long userId, TicketType type, TicketStatus status, String subject, String message, String createdAt){
        this.id = id; this.userId = userId; this.type = type; this.status = status;
        this.subject = subject; this.message = message; this.createdAt = createdAt;
    }

    // getters/setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public TicketType getType() { return type; }
    public void setType(TicketType type) { this.type = type; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
