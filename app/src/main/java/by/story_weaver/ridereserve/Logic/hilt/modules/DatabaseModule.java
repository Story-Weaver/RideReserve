package by.story_weaver.ridereserve.Logic.hilt.modules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Singleton;

import by.story_weaver.ridereserve.Logic.data.dao.BookingDao;
import by.story_weaver.ridereserve.Logic.data.dao.CommunicationLogDao;
import by.story_weaver.ridereserve.Logic.data.dao.DatabaseHelper;
import by.story_weaver.ridereserve.Logic.data.dao.RouteDao;
import by.story_weaver.ridereserve.Logic.data.dao.SeatDao;
import by.story_weaver.ridereserve.Logic.data.dao.SupportDao;
import by.story_weaver.ridereserve.Logic.data.dao.TripDao;
import by.story_weaver.ridereserve.Logic.data.dao.UserDao;
import by.story_weaver.ridereserve.Logic.data.dao.VehicleDao;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public SQLiteOpenHelper provideDatabase(@ApplicationContext Context context) {
        // возвращаем наш DatabaseHelper (наследник SQLiteOpenHelper)
        return new DatabaseHelper(context);
    }

    @Provides
    @Singleton
    public SQLiteDatabase provideWritableDatabase(SQLiteOpenHelper helper) {
        return helper.getWritableDatabase();
    }

    @Provides
    @Singleton
    public UserDao provideUserDao(SQLiteDatabase db) {
        return new UserDao(db);
    }

    @Provides
    @Singleton
    public RouteDao provideRouteDao(SQLiteDatabase db) {
        return new RouteDao(db);
    }

    @Provides
    @Singleton
    public VehicleDao provideVehicleDao(SQLiteDatabase db) {
        return new VehicleDao(db);
    }

    @Provides
    @Singleton
    public SeatDao provideSeatDao(SQLiteDatabase db) {
        return new SeatDao(db);
    }

    @Provides
    @Singleton
    public TripDao provideTripDao(SQLiteDatabase db) {
        return new TripDao(db);
    }

    @Provides
    @Singleton
    public BookingDao provideBookingDao(SQLiteDatabase db) {
        return new BookingDao(db);
    }

    @Provides
    @Singleton
    public SupportDao provideSupportDao(SQLiteDatabase db) {
        return new SupportDao(db);
    }

    @Provides
    @Singleton
    public CommunicationLogDao provideCommunicationLogDao(SQLiteDatabase db) {
        return new CommunicationLogDao(db);
    }
}
