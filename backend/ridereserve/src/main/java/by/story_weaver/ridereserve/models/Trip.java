package by.story_weaver.ridereserve.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

import by.story_weaver.ridereserve.models.enums.TripStatus;

@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "route_id", nullable = false)
    private Long routeId;
    
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;
    
    @Column(name = "driver_id", nullable = false)
    private Long driverId;
    
    @Column(name = "departure_time", nullable = false)
    private String departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private String arrivalTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.SCHEDULED;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    @Column(nullable = false)
    private BigDecimal price;

    // Конструкторы
    public Trip() {}

    public Trip(Long routeId, Long vehicleId, Long driverId, String departureTime, 
                String arrivalTime, TripStatus status, BigDecimal price) {
        this.routeId = routeId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}