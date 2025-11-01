package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import by.story_weaver.ridereserve.models.*;
import by.story_weaver.ridereserve.services.AuthService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
public ResponseEntity<User> login(@RequestBody EnterRequest request) {
    try {
        if (request == null) return ResponseEntity.badRequest().build();
        User response = authService.login(request);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping("/register")
public ResponseEntity<User> register(@RequestBody User request) {
    try {
        if (request == null) return ResponseEntity.badRequest().build();
        User response = authService.register(request);
        return response != null ? ResponseEntity.status(HttpStatus.CREATED).body(response) : ResponseEntity.badRequest().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

}