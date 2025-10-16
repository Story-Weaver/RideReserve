package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import android.util.Log;

import java.util.List;

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

    @Override
    public List<User> getAll() { return dao.getAll(); }
    @Override
    public User getUserByEmail(String email) { return dao.getByEmail(email); }
    @Override
    public void updateUser(User user) { dao.update(user); }
    @Override
    public void exit(){
            dao.exit();
    }

    @Override
    public void setUserInSystem(long id){
        dao.setUserInSystem(id);
    }
    @Override
    public int getUserInSystem(){
        return dao.getIdUserInSystem();
    }
}

