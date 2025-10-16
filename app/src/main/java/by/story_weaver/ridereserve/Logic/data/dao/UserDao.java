package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Users.COL_IN_SYSTEM)),
                        UserRole.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.Users.COL_ROLE)))
                );
            }
        }
        return null;
    }

    public void exit(){
        String checkQuery = "SELECT * FROM " + DatabaseContract.Users.TABLE_NAME +
                " WHERE " + DatabaseContract.Users.COL_IN_SYSTEM + " = 1";
        Cursor cursor = db.rawQuery(checkQuery, null);
        try (cursor) {
            if (cursor.moveToFirst()) {
                ContentValues resetValues = new ContentValues();
                resetValues.put(DatabaseContract.Users.COL_IN_SYSTEM, 0);
                db.update(DatabaseContract.Users.TABLE_NAME, resetValues, null, null);
            }
        }
    }
    public int getIdUserInSystem(){
        int id = -1;
        String query = "SELECT " + DatabaseContract.Users.COL_ID + " FROM " + DatabaseContract.Users.TABLE_NAME +
                " WHERE " + DatabaseContract.Users.COL_IN_SYSTEM + " = 1";
        Cursor cursor = db.rawQuery(query, null);
        try (cursor){
            if(cursor != null && cursor.moveToNext()){
                id = cursor.getInt(0);
            }
        }
        return id;
    }
    public void setUserInSystem(long id){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.COL_IN_SYSTEM, 1);
        String selection = DatabaseContract.Users.COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(DatabaseContract.Users.TABLE_NAME, values, selection, selectionArgs);
    }
    public boolean isTableEmpty(){
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.Users.TABLE_NAME, null)){
            if(cursor.moveToFirst()){
                return cursor.getInt(0) == 0;
            }
        }
        return true;
    }

    public List<User> getAll(){
        List<User> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Users.TABLE_NAME, null)) {
            while (c.moveToNext()) {
                list.add(readUserFromCursor(c));
            }
        }
        return list;
    }

    public User getByEmail(String email){
        try (Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Users.TABLE_NAME +
                " WHERE " + DatabaseContract.Users.COL_EMAIL + " = ?", new String[]{email})) {
            if (c.moveToNext()) return readUserFromCursor(c);
        }
        return null;
    }

    public int update(User u){
        ContentValues v = new ContentValues();
        v.put(DatabaseContract.Users.COL_EMAIL, u.getEmail());
        v.put(DatabaseContract.Users.COL_PASSWORD, u.getPassword());
        v.put(DatabaseContract.Users.COL_FULL_NAME, u.getFullName());
        v.put(DatabaseContract.Users.COL_PHONE, u.getPhone());
        v.put(DatabaseContract.Users.COL_ROLE, u.getRole() == null ? null : u.getRole().name());
        return db.update(DatabaseContract.Users.TABLE_NAME, v, DatabaseContract.Users.COL_ID + " = ?", new String[]{String.valueOf(u.getId())});
    }
    private User readUserFromCursor(@NonNull Cursor c){
        int idIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_ID);
        int emailIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_EMAIL);
        int passIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_PASSWORD);
        int nameIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_FULL_NAME);
        int phoneIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_PHONE);
        int inSystemIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_IN_SYSTEM);
        int roleIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_ROLE);

        int id = c.getInt(idIdx);
        String email = c.isNull(emailIdx) ? null : c.getString(emailIdx);
        String password = c.isNull(passIdx) ? null : c.getString(passIdx);
        String fullName = c.isNull(nameIdx) ? null : c.getString(nameIdx);
        String phone = c.isNull(phoneIdx) ? null : c.getString(phoneIdx);
        int inSystem = c.getInt(inSystemIdx);
        String roleStr = c.isNull(roleIdx) ? null : c.getString(roleIdx);
        UserRole role = null;
        try {
            if (roleStr != null) role = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException ignored) {
        }

        return new User(id, email, password, fullName, phone,inSystem,  role);
    }
}