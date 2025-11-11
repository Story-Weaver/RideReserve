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
        values.put(DatabaseContract.Users.COL_ID, user.getId());
        values.put(DatabaseContract.Users.COL_EMAIL, user.getEmail());
        values.put(DatabaseContract.Users.COL_PASSWORD, user.getPassword());
        values.put(DatabaseContract.Users.COL_FULL_NAME, user.getFullName());
        values.put(DatabaseContract.Users.COL_PHONE, user.getPhone());
        values.put(DatabaseContract.Users.COL_ROLE, user.getRole().name());
        values.put(DatabaseContract.Users.COL_IN_SYSTEM, user.getInSystem());
        values.put(DatabaseContract.Users.COL_DELETED, user.getDeleted() ? 1 : 0);
        db.insertWithOnConflict(DatabaseContract.Users.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeUser(long id){
        db.delete(DatabaseContract.Users.TABLE_NAME, DatabaseContract.Users.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void softDeleteUser(long id) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.COL_DELETED, 1);
        db.update(DatabaseContract.Users.TABLE_NAME, values, DatabaseContract.Users.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public User getUser(long id){
        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseContract.Users.TABLE_NAME + " WHERE " +
                        DatabaseContract.Users.COL_ID + " = ?", new String[]{String.valueOf(id)})) {
            if(cursor != null && cursor.moveToNext()){
                return readUserFromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean exit() {
        try {
            String checkQuery = "SELECT * FROM " + DatabaseContract.Users.TABLE_NAME +
                    " WHERE " + DatabaseContract.Users.COL_IN_SYSTEM + " = 1";
            Cursor cursor = db.rawQuery(checkQuery, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        ContentValues resetValues = new ContentValues();
                        resetValues.put(DatabaseContract.Users.COL_IN_SYSTEM, 0);
                        int updatedRows = db.update(DatabaseContract.Users.TABLE_NAME, resetValues, null, null);
                        return updatedRows > 0; // Возвращаем true если были обновленные строки
                    } else {
                        return true; // Нет пользователей в системе - считаем успехом
                    }
                } finally {
                    cursor.close();
                }
            }
            return false; // Ошибка курсора
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Ошибка операции
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
        } catch (Exception e) {
            e.printStackTrace();
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
            if(cursor != null && cursor.moveToFirst()){
                return cursor.getInt(0) == 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<User> getAll(){
        List<User> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Users.TABLE_NAME, null)) {
            if (c != null) {
                while (c.moveToNext()) {
                    list.add(readUserFromCursor(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllActiveUsers() {
        List<User> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Users.TABLE_NAME +
                " WHERE " + DatabaseContract.Users.COL_DELETED + " = 0", null)) {
            if (c != null) {
                while (c.moveToNext()) {
                    list.add(readUserFromCursor(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public User getByEmail(String email){
        try (Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Users.TABLE_NAME +
                " WHERE " + DatabaseContract.Users.COL_EMAIL + " = ?", new String[]{email})) {
            if (c != null && c.moveToNext()) {
                return readUserFromCursor(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int update(User u){
        try {
            ContentValues v = new ContentValues();
            v.put(DatabaseContract.Users.COL_EMAIL, u.getEmail());
            v.put(DatabaseContract.Users.COL_PASSWORD, u.getPassword());
            v.put(DatabaseContract.Users.COL_FULL_NAME, u.getFullName());
            v.put(DatabaseContract.Users.COL_PHONE, u.getPhone());
            v.put(DatabaseContract.Users.COL_ROLE, u.getRole() == null ? null : u.getRole().name());
            v.put(DatabaseContract.Users.COL_IN_SYSTEM, u.getInSystem());
            v.put(DatabaseContract.Users.COL_DELETED, u.getDeleted() ? 1 : 0);
            return db.update(DatabaseContract.Users.TABLE_NAME, v, DatabaseContract.Users.COL_ID + " = ?", new String[]{String.valueOf(u.getId())});
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @SuppressLint("Range")
    private User readUserFromCursor(@NonNull Cursor c){
        try {
            int idIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_ID);
            int emailIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_EMAIL);
            int passIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_PASSWORD);
            int nameIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_FULL_NAME);
            int phoneIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_PHONE);
            int inSystemIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_IN_SYSTEM);
            int roleIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_ROLE);
            int deletedIdx = c.getColumnIndexOrThrow(DatabaseContract.Users.COL_DELETED);

            int id = c.getInt(idIdx);
            String email = c.isNull(emailIdx) ? null : c.getString(emailIdx);
            String password = c.isNull(passIdx) ? null : c.getString(passIdx);
            String fullName = c.isNull(nameIdx) ? null : c.getString(nameIdx);
            String phone = c.isNull(phoneIdx) ? null : c.getString(phoneIdx);
            int inSystem = c.getInt(inSystemIdx);
            boolean deleted = c.getInt(deletedIdx) == 1;
            String roleStr = c.isNull(roleIdx) ? null : c.getString(roleIdx);
            UserRole role = null;
            try {
                if (roleStr != null) role = UserRole.valueOf(roleStr);
            } catch (IllegalArgumentException ignored) {
            }

            User user = new User(id, email, password, fullName, phone, inSystem, role);
            user.setDeleted(deleted);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}