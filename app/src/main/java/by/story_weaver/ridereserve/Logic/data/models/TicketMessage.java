package by.story_weaver.ridereserve.Logic.data.models;

public class TicketMessage {
    private long id;
    private long ticketId;
    private long authorId;
    private String text;
    private String createdAt;

    public TicketMessage() {}
    public TicketMessage(long id, long ticketId, long authorId, String text, String createdAt){
        this.id = id; this.ticketId = ticketId; this.authorId = authorId; this.text = text; this.createdAt = createdAt;
    }

    // getters/setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getTicketId() { return ticketId; }
    public void setTicketId(long ticketId) { this.ticketId = ticketId; }
    public long getAuthorId() { return authorId; }
    public void setAuthorId(long authorId) { this.authorId = authorId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
