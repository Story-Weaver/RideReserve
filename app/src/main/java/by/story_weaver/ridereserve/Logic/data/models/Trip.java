package by.story_weaver.ridereserve.Logic.data.models;

import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;

public class Trip {
    private long id;
    private long routeId;
    private long vehicleId;
    private long driverId;
    private String departureTime;
    private String arrivalTime; //время отправки
    private TripStatus status;
    private double price;
    private boolean deleted = false;


    public Trip() {}
    public Trip(long routeId, long vehicleId, long driverId, String departureTime, String arrivalTime, TripStatus status, double price){
        this.routeId = routeId; this.vehicleId = vehicleId; this.driverId = driverId;
        this.departureTime = departureTime; this.arrivalTime = arrivalTime; this.status = status;
        this.price = price;
    }
    public Trip(long id, long routeId, long vehicleId, long driverId, String departureTime, String arrivalTime, TripStatus status, double price){
        this.id = id; this.routeId = routeId; this.vehicleId = vehicleId; this.driverId = driverId;
        this.departureTime = departureTime; this.arrivalTime = arrivalTime; this.status = status;
        this.price = price;
    }

    // getters/setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getRouteId() { return routeId; }
    public void setRouteId(long routeId) { this.routeId = routeId; }
    public long getVehicleId() { return vehicleId; }
    public void setVehicleId(long vehicleId) { this.vehicleId = vehicleId; }
    public long getDriverId() { return driverId; }
    public void setDriverId(long driverId) { this.driverId = driverId; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public boolean getDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}