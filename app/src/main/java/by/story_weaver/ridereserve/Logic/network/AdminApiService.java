package by.story_weaver.ridereserve.Logic.network;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.AdminStats;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AdminApiService {
    @GET("admin/stats")
    Call<AdminStats> getAdminStats();
    @GET("admin/activetrips")
    Call<List<Trip>> getActiveTrips();


    @GET("admin/users")
    Call<List<User>> getAllUsers();
    @GET("admin/users/{id}")
    Call<User> getUserById(@Path("id") long id);
    @POST("admin/users")
    Call<User> createUser(@Body User user);
    @PUT("admin/users/{id}")
    Call<User> updateUser(@Path("id") long id, @Body User user);
    @DELETE("admin/users/{id}")
    Call<Void> deleteUser(@Path("id") long id);


    @GET("admin/routes")
    Call<List<Route>> getAllRoutes();
    @GET("admin/routes/{id}")
    Call<Route> getRouteById(@Path("id") long id);
    @POST("admin/routes")
    Call<Route> createRoute(@Body Route route);
    @PUT("admin/routes/{id}")
    Call<Route> updateRoute(@Path("id") long id, @Body Route route);
    @DELETE("admin/routes/{id}")
    Call<Void> deleteRoute(@Path("id") long id);


    @GET("admin/vehicles")
    Call<List<Vehicle>> getAllVehicles();
    @GET("admin/vehicles/{id}")
    Call<Vehicle> getVehicleById(@Path("id") long id);
    @POST("admin/vehicles")
    Call<Vehicle> createVehicle(@Body Vehicle vehicle);
    @PUT("admin/vehicles/{id}")
    Call<Vehicle> updateVehicle(@Path("id") long id, @Body Vehicle vehicle);
    @DELETE("admin/vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") long id);


    @GET("admin/trips")
    Call<List<Trip>> getAllTrips();
    @GET("admin/trips/{id}")
    Call<Trip> getTripById(@Path("id") long id);
    @POST("admin/trips")
    Call<Trip> createTrip(@Body Trip trip);
    @PUT("admin/trips/{id}")
    Call<Trip> updateTrip(@Path("id") long id, @Body Trip trip);
    @DELETE("admin/trips/{id}")
    Call<Void> deleteTrip(@Path("id") long id);


    @GET("admin/bookings")
    Call<List<Booking>> getAllBookings();
    @GET("admin/bookings/{id}")
    Call<Booking> getBookingById(@Path("id") long id);
    @POST("admin/bookings")
    Call<Booking> createBooking(@Body Booking booking);
    @PUT("admin/bookings/{id}")
    Call<Booking> updateBooking(@Path("id") long id, @Body Booking booking);
    @DELETE("admin/bookings/{id}")
    Call<Void> deleteBooking(@Path("id") long id);
}
