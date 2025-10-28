package by.story_weaver.ridereserve.Logic.network.impl;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.AdminStats;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AdminApiService {
    @GET("admin/stats")
    Call<AdminStats> getAdminStats();

    @GET("admin/active-trips")
    Call<List<Trip>> getActiveTrips();
}
