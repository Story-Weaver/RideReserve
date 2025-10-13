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
        db.insertWithOnConflict(DatabaseContract.Trips.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeTrip(int id){
        db.delete(DatabaseContract.Trips.TABLE_NAME, DatabaseContract.Trips.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public Trip getTrip(int id){
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
                        TripStatus.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.Trips.COL_STATUS))
                ));
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Trip> getTripsByRoute(int routeId){
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
                        TripStatus.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.Trips.COL_STATUS)))
                ));
            }
        }
        return list;
    }
}
