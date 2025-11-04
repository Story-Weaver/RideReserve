package by.story_weaver.ridereserve.models;

import jakarta.persistence.*;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String origin;
    
    @Column(nullable = false)
    private String destination;
    
    @Column(name = "stops_json", columnDefinition = "TEXT")
    private String stopsJson;
    
    private Double distance;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    private String time;

    // Конструкторы
    public Route() {}

    public Route(String name, String origin, String destination, Double distance, String time, String stopsJson) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.time = time;
        this.stopsJson = stopsJson;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public String getStopsJson() { return stopsJson; }
    public void setStopsJson(String stopsJson) { this.stopsJson = stopsJson; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}