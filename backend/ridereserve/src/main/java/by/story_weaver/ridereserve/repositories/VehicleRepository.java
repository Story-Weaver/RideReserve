package by.story_weaver.ridereserve.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import by.story_weaver.ridereserve.models.*;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query(value = "SELECT * FROM vehicles WHERE plate_number = :plate_number", nativeQuery = true)
    Vehicle getVehicleByNum(@Param("plate_number") String plate_number);

    @Query(value = "SELECT * FROM vehicles WHERE id = :id", nativeQuery = true)
    Vehicle getVehicleById(@Param("id") long id);
}
