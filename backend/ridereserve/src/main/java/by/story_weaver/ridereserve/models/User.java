package by.story_weaver.ridereserve.models;

import by.story_weaver.ridereserve.models.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    private String phone;
    
    @Column(name = "in_system")
    private Integer inSystem = 0;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.PASSENGER;

    // Конструкторы
    public User() {}

    public User(String email, String password, String fullName, String phone, UserRole role) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Integer getInSystem() { return inSystem; }
    public void setInSystem(Integer inSystem) { this.inSystem = inSystem; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}