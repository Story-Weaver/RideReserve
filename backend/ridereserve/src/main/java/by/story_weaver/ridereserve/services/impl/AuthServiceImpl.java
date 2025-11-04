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
        String password = request.getPassword();
        
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email is empty");
            return null;
        }
        
        if (password == null) {
            System.out.println("Password is null");
            return null;
        }
        
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            System.out.println("User not found with email: " + email);
            return null;
        }
        
        if (user.getPassword().equals(password)) {
            return user;
        } else {
            System.out.println("Invalid password for user: " + email);
            return null;
        }
        
    } catch (Exception e) {
        System.out.println("Login error: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

    @Override
    public User register(User user) {
    try {
        User existingUser = userRepository.existsByEmail(user.getEmail());
        if (existingUser != null) {
            return null;
        }
        user.setId(null);
        
        return userRepository.save(user);
    } catch (Exception e) {
        System.out.println("Error during registration: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
    
}
