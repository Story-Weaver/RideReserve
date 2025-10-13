package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import by.story_weaver.ridereserve.Logic.data.models.TicketMessage;

public class TicketMessageDao {
    private final SQLiteDatabase db;

    @Inject
    public TicketMessageDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addMessage(@NonNull TicketMessage msg){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TicketMessages.COL_TICKET_ID, msg.getTicketId());
        values.put(DatabaseContract.TicketMessages.COL_AUTHOR_ID, msg.getAuthorId());
        values.put(DatabaseContract.TicketMessages.COL_TEXT, msg.getText());
        values.put(DatabaseContract.TicketMessages.COL_CREATED_AT, msg.getCreatedAt());
        db.insertWithOnConflict(DatabaseContract.TicketMessages.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @SuppressLint("Range")
    public List<TicketMessage> getMessagesByTicket(int ticketId){
        List<TicketMessage> list = new ArrayList<>();
        try(Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.TicketMessages.TABLE_NAME +
                " WHERE " + DatabaseContract.TicketMessages.COL_TICKET_ID + " = ?", new String[]{String.valueOf(ticketId)})){
            while(cursor.moveToNext()){
                list.add(new TicketMessage(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.TicketMessages.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.TicketMessages.COL_TICKET_ID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.TicketMessages.COL_AUTHOR_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.TicketMessages.COL_TEXT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.TicketMessages.COL_CREATED_AT))
                ));
            }
        }
        return list;
    }

    public void removeMessage(int id){
        db.delete(DatabaseContract.TicketMessages.TABLE_NAME, DatabaseContract.TicketMessages.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
