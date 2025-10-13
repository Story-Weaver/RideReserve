package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.CommunicationLog;

public class CommunicationLogDao {
    private final SQLiteDatabase db;

    public CommunicationLogDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addLog(@NonNull CommunicationLog log){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.CommunicationLogs.COL_BOOKING_ID, log.getBookingId());
        values.put(DatabaseContract.CommunicationLogs.COL_FROM_USER_ID, log.getFromUserId());
        values.put(DatabaseContract.CommunicationLogs.COL_TO_USER_ID, log.getToUserId());
        values.put(DatabaseContract.CommunicationLogs.COL_METHOD, log.getMethod());
        values.put(DatabaseContract.CommunicationLogs.COL_TIMESTAMP, log.getTimestamp());
        values.put(DatabaseContract.CommunicationLogs.COL_NOTES, log.getNotes());
        db.insertWithOnConflict(DatabaseContract.CommunicationLogs.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @SuppressLint("Range")
    public List<CommunicationLog> getLogsByBooking(int bookingId){
        List<CommunicationLog> list = new ArrayList<>();
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.CommunicationLogs.TABLE_NAME +
                " WHERE " + DatabaseContract.CommunicationLogs.COL_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)})){
            while(cursor.moveToNext()){
                list.add(new CommunicationLog(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.CommunicationLogs.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.CommunicationLogs.COL_BOOKING_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.CommunicationLogs.COL_FROM_USER_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.CommunicationLogs.COL_TO_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.CommunicationLogs.COL_METHOD)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.CommunicationLogs.COL_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.CommunicationLogs.COL_NOTES))
                ));
            }
        }
        return list;
    }

    public void removeLog(int id){
        db.delete(DatabaseContract.CommunicationLogs.TABLE_NAME, DatabaseContract.CommunicationLogs.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
