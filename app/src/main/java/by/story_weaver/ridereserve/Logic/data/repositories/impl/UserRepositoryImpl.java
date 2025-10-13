package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import by.story_weaver.ridereserve.Logic.data.dao.UserDao;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.UserRepository;

@Singleton
public class UserRepositoryImpl implements UserRepository {
    private final UserDao dao;

    @Inject
    public UserRepositoryImpl(UserDao dao) {
        this.dao = dao;
    }

    @Override
    public void addUser(User user) {
        dao.addUser(user);
    }

    @Override
    public void removeUser(int id) {
        dao.removeUser(id);
    }

    @Override
    public User getUser(int id) {
        return dao.getUser(id);
    }

    @Override
    public boolean isTableEmpty() {
        return dao.isTableEmpty();
    }
}

