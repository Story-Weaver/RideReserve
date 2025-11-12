package by.story_weaver.ridereserve.Logic.network;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TripApiService {
    @GET("trips")
    Call<List<Trip>> getAllTrips();
    @GET("trips/{id}")
    Call<Trip> getTripById(@Path("id") Long id);
    @GET("trips/route/{routeId}")
    Call<List<Trip>> getTripsByRoute(@Path("routeId") Long routeId);
    @GET("trips/status/{status}")
    Call<List<Trip>> getTripsByStatus(@Path("status") TripStatus status);
    @GET("trips/active/{count}")
    Call<List<Trip>> getActiveTrips(@Path("count") int count);
    @POST("trips")
    Call<Trip> createTrip(@Body Trip trip);
    @PUT("trips")
    Call<Trip> updateTrip(@Body Trip trip);
    @PUT("trips/{id}/status")
    Call<Trip> updateTripStatus(@Path("id") Long id, @Query("status") TripStatus status);
    @DELETE("trips/{id}")
    Call<Void> deleteTrip(@Path("id") Long id);
}
