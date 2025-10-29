package by.story_weaver.ridereserve.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.EnterRequest;
import by.story_weaver.ridereserve.models.User;
import by.story_weaver.ridereserve.repositories.UserRepository;
import by.story_weaver.ridereserve.services.AuthService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User login(EnterRequest request) {
        try {
            String email = request.getEmail();
            String pass = request.getPassword();
            User user = userRepository.findByEmail(email);
            if(user.getPassword().equals(pass)){
                return user;
            } else{
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User register(User user) {
        try {
            if(userRepository.existsByEmail(user.getEmail())){
                return null;
            } else {
                return userRepository.save(user);
            }
        } catch (Exception e) {
            return null;
        }
    }
    
}
