package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.Route;

public class RouteDao {
    private final SQLiteDatabase db;

    public RouteDao(SQLiteDatabase db){
        this.db = db;
    }

    public void addRoute(@NonNull Route route){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Routes.COL_NAME, route.getName());
        values.put(DatabaseContract.Routes.COL_ORIGIN, route.getOrigin());
        values.put(DatabaseContract.Routes.COL_DESTINATION, route.getDestination());
        values.put(DatabaseContract.Routes.COL_STOPS_JSON, route.getStopsJson());
        db.insertWithOnConflict(DatabaseContract.Routes.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void removeRoute(int id){
        db.delete(DatabaseContract.Routes.TABLE_NAME, DatabaseContract.Routes.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public Route getRoute(int id){
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Routes.TABLE_NAME + " WHERE " +
                DatabaseContract.Routes.COL_ID + " = ?", new String[]{String.valueOf(id)})) {
            if(cursor.moveToNext()){
                return new Route(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Routes.COL_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Routes.COL_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Routes.COL_ORIGIN)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Routes.COL_DESTINATION)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Routes.COL_STOPS_JSON))
                );
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Route> getAllRoutes(){
        List<Route> routes = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Routes.TABLE_NAME, null)){
            while(cursor.moveToNext()){
                routes.add(new Route(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Routes.COL_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Routes.COL_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Routes.COL_ORIGIN)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Routes.COL_DESTINATION)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Routes.COL_STOPS_JSON))
                ));
            }
        }
        return routes;
    }
}
