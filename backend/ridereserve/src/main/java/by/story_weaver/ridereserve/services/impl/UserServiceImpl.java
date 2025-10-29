package by.story_weaver.ridereserve.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.User;
import by.story_weaver.ridereserve.repositories.UserRepository;
import by.story_weaver.ridereserve.services.UserService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }
   
    @Override
    public User getUserById(long id) {
        try {
            return userRepository.getUserById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User createUser(User user) {
        try {
            userRepository.save(user);
            return userRepository.findByEmail(user.getEmail());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            userRepository.save(user);
            return userRepository.findByEmail(user.getEmail());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteUser(long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
