package by.story_weaver.ridereserve.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

import by.story_weaver.ridereserve.models.enums.BookingStatus;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trip_id", nullable = false)
    private Long tripId;
    
    @Column(name = "passenger_id", nullable = false)
    private Long passengerId;
    
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;
    
    @Column(name = "child_seat_needed")
    private Boolean childSeatNeeded = false;
    
    @Column(name = "has_pet")
    private Boolean hasPet = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    // Конструкторы
    public Booking() {}

    public Booking(long tripId, long passengerId, Integer seatNumber, Boolean childSeatNeeded, 
                   Boolean hasPet, BookingStatus status, BigDecimal price) {
        this.tripId = tripId;
        this.passengerId = passengerId;
        this.seatNumber = seatNumber;
        this.childSeatNeeded = childSeatNeeded;
        this.hasPet = hasPet;
        this.status = status;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public long getTripId() { return tripId; }
    public void setTrip(long tripId) { this.tripId = tripId; }
    
    public long getPassengerId() { return passengerId; }
    public void setPassengerId(long passengerId) { this.passengerId = passengerId; }
    
    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }
    
    public Boolean getChildSeatNeeded() { return childSeatNeeded; }
    public void setChildSeatNeeded(Boolean childSeatNeeded) { this.childSeatNeeded = childSeatNeeded; }
    
    public Boolean getHasPet() { return hasPet; }
    public void setHasPet(Boolean hasPet) { this.hasPet = hasPet; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}