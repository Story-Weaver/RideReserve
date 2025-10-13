package by.story_weaver.ridereserve.Logic.data.models;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;

public class User {
    private long id;
    private String email;
    private String password; // в реальном приложении храните хеш
    private String fullName;
    private String phone;
    private UserRole role;

    public User() {}

    public User(long id, String email, String password, String fullName, String phone, UserRole role) {
        this.id = id; this.email = email; this.password = password;
        this.fullName = fullName; this.phone = phone; this.role = role;
    }

    // getters / setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
