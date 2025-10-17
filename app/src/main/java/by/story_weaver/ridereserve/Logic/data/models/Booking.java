package by.story_weaver.ridereserve.Logic.data.models;

import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;

public class Booking {
    private long id;
    private long tripId;
    private long passengerId;
    private int seatNumber;
    private boolean childSeatNeeded;
    private boolean hasPet;
    private BookingStatus status;
    private String createdAt;

    public Booking() {}
    public Booking(long id, long tripId, long passengerId, int seatNumber,
                   boolean childSeatNeeded, boolean hasPet, BookingStatus status, String createdAt){
        this.id = id; this.tripId = tripId; this.passengerId = passengerId; this.seatNumber = seatNumber;
        this.childSeatNeeded = childSeatNeeded; this.hasPet = hasPet; this.status = status; this.createdAt = createdAt;
    }

    // getters/setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getTripId() { return tripId; }
    public void setTripId(long tripId) { this.tripId = tripId; }
    public long getPassengerId() { return passengerId; }
    public void setPassengerId(long passengerId) { this.passengerId = passengerId; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public boolean isChildSeatNeeded() { return childSeatNeeded; }
    public void setChildSeatNeeded(boolean childSeatNeeded) { this.childSeatNeeded = childSeatNeeded; }
    public boolean isHasPet() { return hasPet; }
    public void setHasPet(boolean hasPet) { this.hasPet = hasPet; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}