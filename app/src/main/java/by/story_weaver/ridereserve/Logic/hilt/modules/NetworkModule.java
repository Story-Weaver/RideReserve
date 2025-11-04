package by.story_weaver.ridereserve.Logic.hilt.modules;

import javax.inject.Singleton;

import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.Logic.network.AdminApiService;
import by.story_weaver.ridereserve.Logic.network.AuthApiService;
import by.story_weaver.ridereserve.Logic.network.BookingApiService;
import by.story_weaver.ridereserve.Logic.network.DriverApiService;
import by.story_weaver.ridereserve.Logic.network.RouteApiService;
import by.story_weaver.ridereserve.Logic.network.TripApiService;
import by.story_weaver.ridereserve.Logic.network.UserApiService;
import by.story_weaver.ridereserve.Logic.network.VehicleApiService;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    //private static final String BASE_URL = "http://192.168.0.85:8080/api/";
    private static final String BASE_URL = "https://kkvxmvg9-8080.euw.devtunnels.ms/api/";
    //private static final String BASE_URL = "http://192.168.43.146:8080/api/";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public AdminApiService provideAdminApiService(Retrofit retrofit) {
        return retrofit.create(AdminApiService.class);
    }

    @Provides
    @Singleton
    public AuthApiService provideAuthApiService(Retrofit retrofit) {
        return retrofit.create(AuthApiService.class);
    }

    @Provides
    @Singleton
    public BookingApiService provideBookingApiService(Retrofit retrofit) {
        return retrofit.create(BookingApiService.class);
    }

    @Provides
    @Singleton
    public DriverApiService provideDriverApiService(Retrofit retrofit) {
        return retrofit.create(DriverApiService.class);
    }

    @Provides
    @Singleton
    public RouteApiService provideRouteApiService(Retrofit retrofit) {
        return retrofit.create(RouteApiService.class);
    }

    @Provides
    @Singleton
    public TripApiService provideTripApiService(Retrofit retrofit) {
        return retrofit.create(TripApiService.class);
    }

    @Provides
    @Singleton
    public UserApiService provideUserApiService(Retrofit retrofit) {
        return retrofit.create(UserApiService.class);
    }

    @Provides
    @Singleton
    public VehicleApiService provideVehicleApiService(Retrofit retrofit) {
        return retrofit.create(VehicleApiService.class);
    }
}