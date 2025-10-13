package by.story_weaver.ridereserve.Logic.data.models;

public class Seat {
    private long id;
    private long vehicleId;
    private int seatNumber; // например 1,2,3...
    private String tag;     // опционально: "window", "aisle" и т.п.

    public Seat() {}
    public Seat(long id, long vehicleId, int seatNumber, String tag){
        this.id = id; this.vehicleId = vehicleId; this.seatNumber = seatNumber; this.tag = tag;
    }

    // getters/setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getVehicleId() { return vehicleId; }
    public void setVehicleId(long vehicleId) { this.vehicleId = vehicleId; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
