package by.story_weaver.ridereserve.Logic.data.dao;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        values.put(DatabaseContract.Bookings.COL_PRICE, booking.getPrice());
        db.insertWithOnConflict(DatabaseContract.Bookings.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    @SuppressLint("Range")
    public boolean hasBookingForUserAndTrip(long passengerId, long tripId) {
        String query = "SELECT COUNT(*) as count FROM " + DatabaseContract.Bookings.TABLE_NAME +
                " WHERE " + DatabaseContract.Bookings.COL_PASSENGER_ID + " = ? AND " +
                DatabaseContract.Bookings.COL_TRIP_ID + " = ? AND " +
                DatabaseContract.Bookings.COL_STATUS + " IN (?, ?)";

        String[] selectionArgs = {
                String.valueOf(passengerId),
                String.valueOf(tripId),
                BookingStatus.CONFIRMED.name(),
                BookingStatus.PENDING.name()
        };

        try (Cursor cursor = db.rawQuery(query, selectionArgs)) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public void removeBooking(long id){
        db.delete(DatabaseContract.Bookings.TABLE_NAME, DatabaseContract.Bookings.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public Booking getBooking(long id){
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
                        cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Bookings.COL_PRICE))
                );
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Booking> getBookingsByTrip(long tripId){
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
                        cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Bookings.COL_PRICE))
                ));
            }
        }
        return list;
    }
    @SuppressLint("Range")
    public List<Booking> getAll(){
        List<Booking> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Bookings.TABLE_NAME, null)) {
            while (c.moveToNext()) {
                list.add(new Booking(
                        c.getInt(c.getColumnIndex(DatabaseContract.Bookings.COL_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Bookings.COL_TRIP_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Bookings.COL_PASSENGER_ID)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Bookings.COL_SEAT_NUMBER)),
                        c.getInt(c.getColumnIndex(DatabaseContract.Bookings.COL_CHILD_SEAT_NEEDED)) == 1,
                        c.getInt(c.getColumnIndex(DatabaseContract.Bookings.COL_HAS_PET)) == 1,
                        BookingStatus.valueOf(c.getString(c.getColumnIndex(DatabaseContract.Bookings.COL_STATUS))),
                        c.getDouble(c.getColumnIndex(DatabaseContract.Bookings.COL_PRICE))
                ));
            }
        }
        return list;
    }
    public boolean updateBooking(Booking booking) {
        if (booking == null) return false;
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Bookings.COL_TRIP_ID, booking.getTripId());
        cv.put(DatabaseContract.Bookings.COL_PASSENGER_ID, booking.getPassengerId());
        cv.put(DatabaseContract.Bookings.COL_SEAT_NUMBER, booking.getSeatNumber());
        cv.put(DatabaseContract.Bookings.COL_CHILD_SEAT_NEEDED, booking.isChildSeatNeeded() ? 1 : 0);
        cv.put(DatabaseContract.Bookings.COL_HAS_PET, booking.isHasPet() ? 1 : 0);
        cv.put(DatabaseContract.Bookings.COL_STATUS, booking.getStatus() != null ? booking.getStatus().name() : null);
        cv.put(DatabaseContract.Bookings.COL_PRICE, booking.getPrice());

        String where = DatabaseContract.Bookings.COL_ID + " = ?";
        String[] whereArgs = new String[]{ String.valueOf(booking.getId()) };

        db.beginTransaction();
        try {
            int rows = db.update(DatabaseContract.Bookings.TABLE_NAME, cv, where, whereArgs);
            db.setTransactionSuccessful();
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "updateBooking error", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }
    public boolean updateBookingStatus(long bookingId, BookingStatus status) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Bookings.COL_STATUS, status != null ? status.name() : null);

        String where = DatabaseContract.Bookings.COL_ID + " = ?";
        String[] whereArgs = new String[]{ String.valueOf(bookingId) };

        db.beginTransaction();
        try {
            int rows = db.update(DatabaseContract.Bookings.TABLE_NAME, cv, where, whereArgs);
            db.setTransactionSuccessful();
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "updateBookingStatus error", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }
}
