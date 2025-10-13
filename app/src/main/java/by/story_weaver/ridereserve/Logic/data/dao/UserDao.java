package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.User;

public class UserDao {
    private final SQLiteDatabase db;

    public UserDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addUser(@NonNull User user){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.COL_EMAIL, user.getEmail());
        values.put(DatabaseContract.Users.COL_PASSWORD, user.getPassword());
        values.put(DatabaseContract.Users.COL_FULL_NAME, user.getFullName());
        values.put(DatabaseContract.Users.COL_PHONE, user.getPhone());
        values.put(DatabaseContract.Users.COL_ROLE, user.getRole().name());
        db.insertWithOnConflict(DatabaseContract.Users.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeUser(int id){
        db.delete(DatabaseContract.Users.TABLE_NAME, DatabaseContract.Users.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public User getUser(int id){
        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseContract.Users.TABLE_NAME + " WHERE " +
                        DatabaseContract.Users.COL_ID + " = ?", new String[]{String.valueOf(id)})) {
            if(cursor.moveToNext()){
                return new User(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Users.COL_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Users.COL_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Users.COL_PASSWORD)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Users.COL_FULL_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Users.COL_PHONE)),
                        UserRole.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.Users.COL_ROLE)))
                );
            }
        }
        return null;
    }

    public boolean isTableEmpty(){
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.Users.TABLE_NAME, null)){
            if(cursor.moveToFirst()){
                return cursor.getInt(0) == 0;
            }
        }
        return true;
    }
}