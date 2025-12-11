package by.story_weaver.ridereserve.Logic.data.models;

import androidx.annotation.NonNull;

public class Route {
    private long id;
    private String name;
    private String origin;
    private String destination;
    private String stopsJson;
    private double distance;
    private boolean deleted = false;
    private String time;

    public Route() {}
    public Route(long id, String name, String origin, String destination,double distance, String time, String stopsJson){
        this.id = id; this.name = name; this.origin = origin; this.destination = destination;
        this.distance = distance; this.time = time; this.stopsJson = stopsJson;
    }

    @NonNull
    @Override
    public String toString() {
        if (origin != null && destination != null) {
            return origin + " → " + destination;
        } else if (name != null) {
            return name;
        }
        return "Маршрут";
    }

    // getters/setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getStopsJson() { return stopsJson; }
    public void setStopsJson(String stopsJson) { this.stopsJson = stopsJson; }
    public boolean getDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}