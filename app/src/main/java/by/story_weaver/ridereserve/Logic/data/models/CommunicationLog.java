package by.story_weaver.ridereserve.Logic.data.models;

public class CommunicationLog {
    private final int id;
    private final int bookingId;
    private final int fromUserId;
    private final int toUserId;
    private final String method;     // например, "call", "message", "app_chat"
    private final String timestamp;  // ISO формат, например "2025-10-13T15:00:00"
    private final String notes;      // дополнительные комментарии

    public CommunicationLog(int id, int bookingId, int fromUserId, int toUserId,
                            String method, String timestamp, String notes) {
        this.id = id;
        this.bookingId = bookingId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.method = method;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public String getMethod() {
        return method;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getNotes() {
        return notes;
    }
}

