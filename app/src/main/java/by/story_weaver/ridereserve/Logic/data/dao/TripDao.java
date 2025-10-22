package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.models.Trip;

public class TripDao {
    private final SQLiteDatabase db;

    public TripDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addTrip(@NonNull Trip trip){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Trips.COL_ROUTE_ID, trip.getRouteId());
        values.put(DatabaseContract.Trips.COL_VEHICLE_ID, trip.getVehicleId());
        values.put(DatabaseContract.Trips.COL_DRIVER_ID, trip.getDriverId());
        values.put(DatabaseContract.Trips.COL_DEPARTURE_TIME, trip.getDepartureTime());
        values.put(DatabaseContract.Trips.COL_ARRIVAL_TIME, trip.getArrivalTime());
        values.put(DatabaseContract.Trips.COL_STATUS, trip.getStatus().name());
        values.put(DatabaseContract.Trips.COL_PRICE, trip.getPrice());
        db.insertWithOnConflict(DatabaseContract.Trips.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeTrip(int id){
        db.delete(DatabaseContract.Trips.TABLE_NAME, DatabaseContract.Trips.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public Trip getTrip(long id){
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Trips.TABLE_NAME +
                " WHERE " + DatabaseContract.Trips.COL_ID + " = ?", new String[]{String.valueOf(id)})){
            if(cursor.moveToNext()){
                return new Trip(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Trips.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Trips.COL_ROUTE_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Trips.COL_VEHICLE_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Trips.COL_DRIVER_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Trips.COL_DEPARTURE_TIME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Trips.COL_ARRIVAL_TIME)),
                        TripStatus.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.Trips.COL_STATUS))),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Trips.COL_PRICE))
                );
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Trip> getTripsByRoute(long routeId){
        List<Trip> list = new ArrayList<>();
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Trips.TABLE_NAME +
                " WHERE " + DatabaseContract.Trips.COL_ROUTE_ID + " = ?", new String[]{String.valueOf(routeId)})){
            while(cursor.moveToNext()){
                list.add(new Trip(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Trips.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Trips.COL_ROUTE_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Trips.COL_VEHICLE_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Trips.COL_DRIVER_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Trips.COL_DEPARTURE_TIME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Trips.COL_ARRIVAL_TIME)),
                        TripStatus.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.Trips.COL_STATUS))),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Trips.COL_PRICE))
                ));
            }
        }
        return list;
    }
    // в TripDao.java
    @SuppressLint("Range")
    public List<Trip> getAll(){
        List<Trip> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Trips.TABLE_NAME, null)) {
            while (c.moveToNext()) {
                list.add(new Trip(
                        c.getInt(c.getColumnIndex(DatabaseContract.Trips.COL_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Trips.COL_ROUTE_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Trips.COL_VEHICLE_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Trips.COL_DRIVER_ID)),
                        c.getString(c.getColumnIndex(DatabaseContract.Trips.COL_DEPARTURE_TIME)),
                        c.getString(c.getColumnIndex(DatabaseContract.Trips.COL_ARRIVAL_TIME)),
                        TripStatus.valueOf(c.getString(c.getColumnIndex(DatabaseContract.Trips.COL_STATUS))),
                        c.getDouble(c.getColumnIndex(DatabaseContract.Trips.COL_PRICE))
                ));
            }
        }
        return list;
    }

    @SuppressLint("Range")
    public List<Trip> getTripsByDriver(int driverId){
        List<Trip> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Trips.TABLE_NAME +
                " WHERE " + DatabaseContract.Trips.COL_DRIVER_ID + " = ?", new String[]{String.valueOf(driverId)})) {
            while (c.moveToNext()) {
                list.add(new Trip(
                        c.getInt(c.getColumnIndex(DatabaseContract.Trips.COL_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Trips.COL_ROUTE_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Trips.COL_VEHICLE_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Trips.COL_DRIVER_ID)),
                        c.getString(c.getColumnIndex(DatabaseContract.Trips.COL_DEPARTURE_TIME)),
                        c.getString(c.getColumnIndex(DatabaseContract.Trips.COL_ARRIVAL_TIME)),
                        TripStatus.valueOf(c.getString(c.getColumnIndex(DatabaseContract.Trips.COL_STATUS))),
                        c.getDouble(c.getColumnIndex(DatabaseContract.Trips.COL_PRICE))
                ));
            }
        }
        return list;
    }

}
