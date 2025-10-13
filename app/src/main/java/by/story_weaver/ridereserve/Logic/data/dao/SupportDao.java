package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.SupportTicket;

public class SupportDao {
    private final SQLiteDatabase db;

    public SupportDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addTicket(@NonNull SupportTicket ticket){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SupportTickets.COL_USER_ID, ticket.getUserId());
        values.put(DatabaseContract.SupportTickets.COL_TYPE, ticket.getType().toString());
        values.put(DatabaseContract.SupportTickets.COL_STATUS, ticket.getStatus().toString());
        values.put(DatabaseContract.SupportTickets.COL_SUBJECT, ticket.getSubject());
        values.put(DatabaseContract.SupportTickets.COL_MESSAGE, ticket.getMessage());
        values.put(DatabaseContract.SupportTickets.COL_CREATED_AT, ticket.getCreatedAt());
        db.insertWithOnConflict(DatabaseContract.SupportTickets.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeTicket(int id){
        db.delete(DatabaseContract.SupportTickets.TABLE_NAME, DatabaseContract.SupportTickets.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public SupportTicket getTicket(int id){
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.SupportTickets.TABLE_NAME +
                " WHERE " + DatabaseContract.SupportTickets.COL_ID + " = ?", new String[]{String.valueOf(id)})){
            if(cursor.moveToNext()){
                return new SupportTicket(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_TYPE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_STATUS)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_SUBJECT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_MESSAGE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_CREATED_AT))
                );
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<SupportTicket> getTicketsByUser(int userId){
        List<SupportTicket> list = new ArrayList<>();
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.SupportTickets.TABLE_NAME +
                " WHERE " + DatabaseContract.SupportTickets.COL_USER_ID + " = ?", new String[]{String.valueOf(userId)})){
            while(cursor.moveToNext()){
                list.add(new SupportTicket(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_TYPE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_STATUS)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_SUBJECT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_MESSAGE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.SupportTickets.COL_CREATED_AT))
                ));
            }
        }
        return list;
    }
}
