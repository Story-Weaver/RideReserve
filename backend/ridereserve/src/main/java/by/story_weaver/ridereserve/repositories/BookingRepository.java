// BookingRepository.java
package by.story_weaver.ridereserve.repositories;

import by.story_weaver.ridereserve.models.*;
import by.story_weaver.ridereserve.models.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query(value = "SELECT * FROM bookings WHERE trip_id = :tripId", nativeQuery = true)
    List<Booking> findByTripId(@Param("tripId") Long tripId);
    
    @Query(value = "SELECT * FROM bookings WHERE passenger_id = :passengerId", nativeQuery = true)
    List<Booking> findByPassengerId(@Param("passengerId") Long passengerId);
    
    @Query(value = "UPDATE bookings SET status = :status WHERE id = :bookingId", nativeQuery = true)
    void updateStatus(@Param("bookingId") Long bookingId, @Param("status") String status);
    
    @Query(value = "SELECT COUNT(*) > 0 FROM bookings WHERE passenger_id = :passengerId AND trip_id = :tripId", nativeQuery = true)
    boolean existsByPassengerAndTrip(@Param("passengerId") Long passengerId, @Param("tripId") Long tripId);
    
    @Query(value = "SELECT * FROM bookings WHERE status = :status", nativeQuery = true)
    List<Booking> findByStatus(@Param("status") String status);

    @Query(value = "SELECT * FROM bookings WHERE id = :id", nativeQuery = true)
    Booking getBokingById(@Param("id") long id);

    int countByStatus(BookingStatus status);
}