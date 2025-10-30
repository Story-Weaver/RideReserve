package by.story_weaver.ridereserve.Logic.network;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.Route;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RouteApiService {
    @GET("routes")
    Call<List<Route>> getAllRoutes();
    @GET("routes/{id}")
    Call<Route> getRouteById(@Path("id") Long id);
    @GET("routes/search/number")
    Call<List<Route>> searchRoutesByNumber(@Query("text") String text);
    @GET("routes/search/points")
    Call<List<Route>> searchRoutesByPoints(@Query("from") String from, @Query("to") String to);
    @GET("routes/get/points")
    Call<List<String>> getRoutePoints(@Query("from") String from, @Query("to") String to);
    @POST("routes")
    Call<Route> createRoute(@Body Route route);
    @PUT("routes")
    Call<Route> updateRoute(@Body Route route);
    @DELETE("routes/{id}")
    Call<Void> deleteRoute(@Path("id") Long id);
}
