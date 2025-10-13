package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;

public class BookingDao {
    private final SQLiteDatabase db;

    public BookingDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addBooking(@NonNull Booking booking){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Bookings.COL_TRIP_ID, booking.getTripId());
        values.put(DatabaseContract.Bookings.COL_PASSENGER_ID, booking.getPassengerId());
        values.put(DatabaseContract.Bookings.COL_SEAT_NUMBER, booking.getSeatNumber());
        values.put(DatabaseContract.Bookings.COL_CHILD_SEAT_NEEDED, booking.isChildSeatNeeded() ? 1 : 0);
        values.put(DatabaseContract.Bookings.COL_HAS_PET, booking.isHasPet() ? 1 : 0);
        values.put(DatabaseContract.Bookings.COL_STATUS, booking.getStatus().name());
        values.put(DatabaseContract.Bookings.COL_CREATED_AT, booking.getCreatedAt());
        db.insertWithOnConflict(DatabaseContract.Bookings.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeBooking(int id){
        db.delete(DatabaseContract.Bookings.TABLE_NAME, DatabaseContract.Bookings.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public Booking getBooking(int id){
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Bookings.TABLE_NAME +
                " WHERE " + DatabaseContract.Bookings.COL_ID + " = ?", new String[]{String.valueOf(id)})){
            if(cursor.moveToNext()){
                return new Booking(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_TRIP_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_PASSENGER_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_SEAT_NUMBER)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_CHILD_SEAT_NEEDED)) == 1,
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_HAS_PET)) == 1,
                        BookingStatus.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.Bookings.COL_STATUS))),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Bookings.COL_CREATED_AT))
                );
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Booking> getBookingsByTrip(int tripId){
        List<Booking> list = new ArrayList<>();
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Bookings.TABLE_NAME +
                " WHERE " + DatabaseContract.Bookings.COL_TRIP_ID + " = ?", new String[]{String.valueOf(tripId)})){
            while(cursor.moveToNext()){
                list.add(new Booking(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_TRIP_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_PASSENGER_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_SEAT_NUMBER)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_CHILD_SEAT_NEEDED)) == 1,
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Bookings.COL_HAS_PET)) == 1,
                        BookingStatus.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.Bookings.COL_STATUS))),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Bookings.COL_CREATED_AT))
                ));
            }
        }
        return list;
    }
}
