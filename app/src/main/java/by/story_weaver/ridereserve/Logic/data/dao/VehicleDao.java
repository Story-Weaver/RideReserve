package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.Vehicle;

public class VehicleDao {
    private final SQLiteDatabase db;

    public VehicleDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addVehicle(@NonNull Vehicle vehicle){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Vehicles.COL_PLATE_NUMBER, vehicle.getPlateNumber());
        values.put(DatabaseContract.Vehicles.COL_MODEL, vehicle.getModel());
        values.put(DatabaseContract.Vehicles.COL_SEATS_COUNT, vehicle.getSeatsCount());
        db.insertWithOnConflict(DatabaseContract.Vehicles.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeVehicle(int id){
        db.delete(DatabaseContract.Vehicles.TABLE_NAME, DatabaseContract.Vehicles.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public Vehicle getVehicle(int id){
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Vehicles.TABLE_NAME +
                " WHERE " + DatabaseContract.Vehicles.COL_ID + " = ?", new String[]{String.valueOf(id)})){
            if(cursor.moveToNext()){
                return new Vehicle(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Vehicles.COL_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Vehicles.COL_PLATE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Vehicles.COL_MODEL)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Vehicles.COL_SEATS_COUNT))
                );
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Vehicle> getAllVehicles(){
        List<Vehicle> list = new ArrayList<>();
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Vehicles.TABLE_NAME, null)){
            while(cursor.moveToNext()){
                list.add(new Vehicle(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Vehicles.COL_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Vehicles.COL_PLATE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Vehicles.COL_MODEL)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Vehicles.COL_SEATS_COUNT))
                ));
            }
        }
        return list;
    }
}
