package by.story_weaver.ridereserve.Logic.data.models;

public class Route {
    private long id;
    private String name;        // например "Маршрут 5"
    private String origin;
    private String destination;
    private String stopsJson;   // опционально: JSON массив остановок

    public Route() {}
    public Route(long id, String name, String origin, String destination, String stopsJson){
        this.id = id; this.name = name; this.origin = origin; this.destination = destination; this.stopsJson = stopsJson;
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
    public String getStopsJson() { return stopsJson; }
    public void setStopsJson(String stopsJson) { this.stopsJson = stopsJson; }
}