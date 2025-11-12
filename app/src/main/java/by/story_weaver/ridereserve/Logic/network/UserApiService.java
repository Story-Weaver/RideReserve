package by.story_weaver.ridereserve.Logic.network;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApiService {
    @GET("users")
    Call<List<User>> getAllUsers();
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);
    @PUT("users/{id}/profile")
    Call<User> updateUserProfile(@Path("id") Long id, @Body User request);
    @GET("users/email/{email}")
    Call<User> getUserByEmail(@Path("email") String email);
    @GET("users/passengers/{tripId}")
    Call<List<User>> getPassengers(@Path("tripId") long tripId);
}
