package by.story_weaver.ridereserve.Logic.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "taxi_app.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // users
        db.execSQL("CREATE TABLE " + DatabaseContract.Users.TABLE_NAME + " (" +
                DatabaseContract.Users.COL_ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.Users.COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                DatabaseContract.Users.COL_PASSWORD + " TEXT NOT NULL, " +
                DatabaseContract.Users.COL_FULL_NAME + " TEXT, " +
                DatabaseContract.Users.COL_PHONE + " TEXT, " +
                DatabaseContract.Users.COL_ROLE + " TEXT NOT NULL, " +
                DatabaseContract.Users.COL_IN_SYSTEM + " INTEGER" +
                ");");

        // routes
        db.execSQL("CREATE TABLE " + DatabaseContract.Routes.TABLE_NAME + " (" +
                DatabaseContract.Routes.COL_ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.Routes.COL_NAME + " TEXT, " +
                DatabaseContract.Routes.COL_ORIGIN + " TEXT, " +
                DatabaseContract.Routes.COL_DESTINATION + " TEXT, " +
                DatabaseContract.Routes.COL_DISTANCE + " DECIMAL, " +
                DatabaseContract.Routes.COL_TIME + " TEXT, " +
                DatabaseContract.Routes.COL_STOPS_JSON + " TEXT" +
                ");");

        // vehicles
        db.execSQL("CREATE TABLE " + DatabaseContract.Vehicles.TABLE_NAME + " (" +
                DatabaseContract.Vehicles.COL_ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.Vehicles.COL_PLATE_NUMBER + " TEXT UNIQUE, " +
                DatabaseContract.Vehicles.COL_MODEL + " TEXT, " +
                DatabaseContract.Vehicles.COL_SEATS_COUNT + " INTEGER" +
                ");");

        // seats
        db.execSQL("CREATE TABLE " + DatabaseContract.Seats.TABLE_NAME + " (" +
                DatabaseContract.Seats.COL_ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.Seats.COL_VEHICLE_ID + " INTEGER NOT NULL, " +
                DatabaseContract.Seats.COL_SEAT_NUMBER + " INTEGER NOT NULL, " +
                DatabaseContract.Seats.COL_TAG + " TEXT, " +
                "UNIQUE(" + DatabaseContract.Seats.COL_VEHICLE_ID + ", " + DatabaseContract.Seats.COL_SEAT_NUMBER + "), " +
                "FOREIGN KEY(" + DatabaseContract.Seats.COL_VEHICLE_ID + ") REFERENCES " +
                DatabaseContract.Vehicles.TABLE_NAME + "(" + DatabaseContract.Vehicles.COL_ID + ") ON DELETE CASCADE" +
                ");");

        // trips
        db.execSQL("CREATE TABLE " + DatabaseContract.Trips.TABLE_NAME + " (" +
                DatabaseContract.Trips.COL_ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.Trips.COL_ROUTE_ID + " INTEGER NOT NULL, " +
                DatabaseContract.Trips.COL_VEHICLE_ID + " INTEGER, " +
                DatabaseContract.Trips.COL_DRIVER_ID + " INTEGER, " +
                DatabaseContract.Trips.COL_DEPARTURE_TIME + " TEXT, " +
                DatabaseContract.Trips.COL_ARRIVAL_TIME + " TEXT, " +
                DatabaseContract.Trips.COL_STATUS + " TEXT, " +
                DatabaseContract.Bookings.COL_PRICE + " DECIMAL, " +
                "FOREIGN KEY(" + DatabaseContract.Trips.COL_ROUTE_ID + ") REFERENCES " +
                DatabaseContract.Routes.TABLE_NAME + "(" + DatabaseContract.Routes.COL_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + DatabaseContract.Trips.COL_VEHICLE_ID + ") REFERENCES " +
                DatabaseContract.Vehicles.TABLE_NAME + "(" + DatabaseContract.Vehicles.COL_ID + ") ON DELETE SET NULL, " +
                "FOREIGN KEY(" + DatabaseContract.Trips.COL_DRIVER_ID + ") REFERENCES " +
                DatabaseContract.Users.TABLE_NAME + "(" + DatabaseContract.Users.COL_ID + ") ON DELETE SET NULL" +
                ");");

        // bookings
        db.execSQL("CREATE TABLE " + DatabaseContract.Bookings.TABLE_NAME + " (" +
                DatabaseContract.Bookings.COL_ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.Bookings.COL_TRIP_ID + " INTEGER NOT NULL, " +
                DatabaseContract.Bookings.COL_PASSENGER_ID + " INTEGER NOT NULL, " +
                DatabaseContract.Bookings.COL_SEAT_NUMBER + " INTEGER, " +
                DatabaseContract.Bookings.COL_CHILD_SEAT_NEEDED + " INTEGER DEFAULT 0, " +
                DatabaseContract.Bookings.COL_HAS_PET + " INTEGER DEFAULT 0, " +
                DatabaseContract.Bookings.COL_STATUS + " TEXT NOT NULL, " +
                DatabaseContract.Bookings.COL_PRICE + " DECIMAL, " +
                "FOREIGN KEY(" + DatabaseContract.Bookings.COL_TRIP_ID + ") REFERENCES " +
                DatabaseContract.Trips.TABLE_NAME + "(" + DatabaseContract.Trips.COL_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + DatabaseContract.Bookings.COL_PASSENGER_ID + ") REFERENCES " +
                DatabaseContract.Users.TABLE_NAME + "(" + DatabaseContract.Users.COL_ID + ") ON DELETE CASCADE" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // простая стратегия миграции (для разработки)
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Bookings.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Trips.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Seats.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Vehicles.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Routes.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Users.TABLE_NAME);
        onCreate(db);
    }
}

