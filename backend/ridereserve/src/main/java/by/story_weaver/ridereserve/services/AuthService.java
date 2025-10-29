package by.story_weaver.ridereserve.services;

import by.story_weaver.ridereserve.models.EnterRequest;
import by.story_weaver.ridereserve.models.User;

public interface AuthService {
    public User login(EnterRequest request);
    public User register(User user);
}
