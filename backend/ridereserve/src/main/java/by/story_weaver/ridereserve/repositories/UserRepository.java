package by.story_weaver.ridereserve.repositories;

import by.story_weaver.ridereserve.models.User;
import by.story_weaver.ridereserve.models.enums.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    User findByEmail(@Param("email") String email);
    
    @Query(value = "SELECT * FROM users WHERE role = :role", nativeQuery = true)
    List<User> findByRole(@Param("role") String role);

    @Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    User getUserById(@Param("id") long id);

    boolean existsByEmail(String email);
    long countByRole(UserRole role);
}