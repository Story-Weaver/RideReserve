package by.story_weaver.ridereserve.repositories;

import by.story_weaver.ridereserve.models.*;
import by.story_weaver.ridereserve.models.enums.TripStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    @Query(value = "SELECT * FROM trips WHERE route_id = :routeId", nativeQuery = true)
    List<Trip> findByRouteId(@Param("routeId") Long routeId);
    
    @Query(value = "SELECT * FROM trips WHERE driver_id = :driverId", nativeQuery = true)
    List<Trip> findByDriverId(@Param("driverId") Long driverId);

    @Query(value = "SELECT * FROM trips WHERE vehicleId = :vehicleId", nativeQuery = true)
    List<Trip> findByVehicleId(@Param("vehicleId") Long vehicleId);
    
    @Query(value = "SELECT * FROM trips WHERE status = :status", nativeQuery = true)
    List<Trip> findByStatus(@Param("status") String status);
    
    @Query(value = "UPDATE trips SET status = :status WHERE id = :tripId", nativeQuery = true)
    void updateStatus(@Param("tripId") Long tripId, @Param("status") String status);
    
    @Query(value = "SELECT * FROM trips WHERE status IN ('IN_PROGRESS', 'SCHEDULED') ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Trip> findActiveTrips(@Param("limit") int limit);

    @Query(value = "SELECT * FROM trips WHERE id = :id", nativeQuery = true)
    Trip getTripById(@Param("id") long id);

    int countByStatus(TripStatus status);
    List<Trip> findByStatus(TripStatus status);

}