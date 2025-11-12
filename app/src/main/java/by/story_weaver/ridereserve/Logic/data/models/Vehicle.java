package by.story_weaver.ridereserve.Logic.data.models;

import androidx.annotation.NonNull;

public class Vehicle {
    private long id;
    private String plateNumber;
    private String model;
    private int seatsCount;
    private boolean deleted = false;

    public Vehicle() {}
    public Vehicle(long id, String plateNumber, String model, int seatsCount){
        this.id = id; this.plateNumber = plateNumber; this.model = model; this.seatsCount = seatsCount;
    }
    @NonNull
    @Override
    public String toString() {
        if (model != null && plateNumber != null) {
            return model + " (" + plateNumber + ")";
        } else if (model != null) {
            return model;
        }
        return "Транспорт";
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
    public boolean getDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
