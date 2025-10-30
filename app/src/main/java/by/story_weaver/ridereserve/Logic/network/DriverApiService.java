package by.story_weaver.ridereserve.Logic.network;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverApiService {
    @GET("driver/{driverId}/profile")
    Call<User> getDriverProfile(@Path("driverId") Long driverId);
    @PUT("driver/{driverId}/profile")
    Call<User> updateDriverProfile(@Path("driverId") Long driverId, @Body User request);
    @GET("driver/{driverId}/trips")
    Call<List<Trip>> getDriverTrips(@Path("driverId") Long driverId);
    @PUT("driver/trips/{tripId}/status")
    Call<Trip> updateTripStatus(@Path("tripId") Long tripId, @Query("status") TripStatus status);
}
