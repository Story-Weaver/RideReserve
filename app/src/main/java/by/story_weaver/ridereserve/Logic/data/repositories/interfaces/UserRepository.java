package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.User;

public interface UserRepository {
    void addUser(User user);
    void removeUser(int id);
    User getUser(int id);
    boolean isTableEmpty();
}
