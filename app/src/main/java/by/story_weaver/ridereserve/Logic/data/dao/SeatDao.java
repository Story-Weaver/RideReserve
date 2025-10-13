package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.Seat;

public class SeatDao {
    private final SQLiteDatabase db;

    public SeatDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addSeat(@NonNull Seat seat){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Seats.COL_VEHICLE_ID, seat.getVehicleId());
        values.put(DatabaseContract.Seats.COL_SEAT_NUMBER, seat.getSeatNumber());
        values.put(DatabaseContract.Seats.COL_TAG, seat.getTag());
        db.insertWithOnConflict(DatabaseContract.Seats.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeSeat(int id){
        db.delete(DatabaseContract.Seats.TABLE_NAME, DatabaseContract.Seats.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public Seat getSeat(int id){
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Seats.TABLE_NAME +
                " WHERE " + DatabaseContract.Seats.COL_ID + " = ?", new String[]{String.valueOf(id)})){
            if(cursor.moveToNext()){
                return new Seat(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Seats.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Seats.COL_VEHICLE_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Seats.COL_SEAT_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Seats.COL_TAG))
                );
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Seat> getSeatsByVehicle(int vehicleId){
        List<Seat> list = new ArrayList<>();
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Seats.TABLE_NAME +
                " WHERE " + DatabaseContract.Seats.COL_VEHICLE_ID + " = ?", new String[]{String.valueOf(vehicleId)})){
            while(cursor.moveToNext()){
                list.add(new Seat(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Seats.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Seats.COL_VEHICLE_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Seats.COL_SEAT_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Seats.COL_TAG))
                ));
            }
        }
        return list;
    }
}
