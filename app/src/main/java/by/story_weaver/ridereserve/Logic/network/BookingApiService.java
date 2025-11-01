package by.story_weaver.ridereserve.Logic.network;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingApiService {
    @GET("bookings")
    Call<List<Booking>> getAllBookings();
    @GET("bookings/{id}")
    Call<Booking> getBookingById(@Path("id") long id);
    @POST("bookings")
    Call<Booking> createBooking(@Body Booking booking);
    @PUT("bookings")
    Call<Booking> updateBooking(@Path("id") long id, @Body Booking booking);
    @DELETE("admin/bookings/{id}")
    Call<Void> deleteBooking(@Path("id") long id);

    @GET("bookings/user/{userId}")
    Call<List<Booking>> getBookingsByUser(@Path("userId") long id);
    @GET("bookings/trip/{tripId}")
    Call<List<Booking>> getBookingsByTrip(@Path("tripId") long id);
    @GET("bookings/status/status")
    Call<List<Booking>> getBookingsByStatus(@Query("status") BookingStatus status);
    @GET("bookings/exists/{passengerId}/{tripId}")
    Call<Boolean> checkBookingExists(@Path("passengerId") long passengerId, @Path("tripId") long tripId);
    @PUT("bookings/{id}/status")
    Call<Booking> updateBookingStatus(@Path("id") long id, @Query("status") String status);
}
