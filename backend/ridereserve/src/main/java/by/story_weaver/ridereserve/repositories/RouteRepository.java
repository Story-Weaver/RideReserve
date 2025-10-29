package by.story_weaver.ridereserve.repositories;

import by.story_weaver.ridereserve.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    @Query(value = "SELECT * FROM routes WHERE name ILIKE '%' || :number || '%'", nativeQuery = true)
    List<Route> findByNameContaining(@Param("number") String number);
    
    @Query(value = "SELECT * FROM routes WHERE origin ILIKE '%' || :from || '%' AND destination ILIKE '%' || :to || '%'", nativeQuery = true)
    List<Route> findByOriginAndDestination(@Param("from") String from, @Param("to") String to);

    @Query(value = "SELECT * FROM routes WHERE id = :id", nativeQuery = true)
    Route getRouteById(@Param("id") long id);
}