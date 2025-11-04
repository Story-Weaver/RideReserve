package by.story_weaver.ridereserve.models;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "plate_number", unique = true, nullable = false)
    private String plateNumber;
    
    @Column(nullable = false)
    private String model;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    @Column(name = "seats_count", nullable = false)
    private Integer seatsCount;

    // Конструкторы
    public Vehicle() {}

    public Vehicle(String plateNumber, String model, Integer seatsCount) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.seatsCount = seatsCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    
    public Integer getSeatsCount() { return seatsCount; }
    public void setSeatsCount(Integer seatsCount) { this.seatsCount = seatsCount; }
}