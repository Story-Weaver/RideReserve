package by.story_weaver.ridereserve.Logic.network;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VehicleApiService {
    @GET("vehicles")
    Call<List<Vehicle>> getAllVehicles();
    @GET("vehicles/{id}")
    Call<Vehicle> getVehicleById(@Path("id") Long id);
    @POST("vehicles")
    Call<Vehicle> createVehicle(@Body Vehicle vehicle);
    @PUT("vehicles/{id}")
    Call<Vehicle> updateVehicle(@Path("id") Long id, @Body Vehicle vehicle);
    @DELETE("vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") Long id);
}
