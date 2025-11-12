package by.story_weaver.ridereserve.services;

import java.util.List;

import by.story_weaver.ridereserve.models.User;

public interface UserService {
    public List<User> getAllUsers();
    public User getUserById(long id);
    public User getUserByEmail(String email);
    public User createUser(User user);
    public User updateUser(User user);
    public boolean deleteUser(long id);
    public List<User> getListPassengers(long tripId);
}
