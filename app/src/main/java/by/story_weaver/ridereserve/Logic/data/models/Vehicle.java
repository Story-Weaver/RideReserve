package by.story_weaver.ridereserve.Logic.data.models;

public class Vehicle {
    private long id;
    private String plateNumber;
    private String model;
    private int seatsCount;

    public Vehicle() {}
    public Vehicle(long id, String plateNumber, String model, int seatsCount){
        this.id = id; this.plateNumber = plateNumber; this.model = model; this.seatsCount = seatsCount;
    }

    // getters/setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getSeatsCount() { return seatsCount; }
    public void setSeatsCount(int seatsCount) { this.seatsCount = seatsCount; }
}
