package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.User;

public interface UserRepository {
    void addUser(User user);
    void removeUser(long id);
    User getUser(long id);
    boolean isTableEmpty();
    List<User> getAll();
    User getUserByEmail(String email);
    void updateUser(User user);
    void exit();
    void editUser(User user);
    long getUserInSystem();
    void setUserInSystem(long id);
}
