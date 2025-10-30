package by.story_weaver.ridereserve.Logic.network;

import by.story_weaver.ridereserve.Logic.data.models.EnterRequest;
import by.story_weaver.ridereserve.Logic.data.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("auth/login")
    Call<User> login(@Body EnterRequest request);
    @POST("auth/register")
    Call<User> register(@Body User user);
}
