package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import by.story_weaver.ridereserve.models.User;
import by.story_weaver.ridereserve.services.UserService;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
public ResponseEntity<List<User>> getAllUsers() {
    try {
        List<User> users = userService.getAllUsers();
        return users != null ? ResponseEntity.ok(users) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
@GetMapping("/passengers/{tripId}")
    public ResponseEntity<?> getListPassengers(@PathVariable long tripId) {
        try {
            List<User> list = userService.getListPassengers(tripId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

@GetMapping("/{id}")
public ResponseEntity<User> getUserById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/{id}/profile")
public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody User request) {
    try {
        if (id == null || request == null) return ResponseEntity.badRequest().build();
        if (request.getId() != null && !request.getId().equals(id)) return ResponseEntity.badRequest().build();
        request.setId(id);
        User updatedUser = userService.updateUser(request);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/email/{email}")
public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    try {
        if (email == null || email.isBlank()) return ResponseEntity.badRequest().build();
        User user = userService.getUserByEmail(email);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

}