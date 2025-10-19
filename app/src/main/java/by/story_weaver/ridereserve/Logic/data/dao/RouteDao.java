package by.story_weaver.ridereserve.Logic.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
    public Route getRoute(long id){
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Routes.TABLE_NAME + " WHERE " +
                DatabaseContract.Routes.COL_ID + " = ?", new String[]{String.valueOf(id)})) {
            if(cursor.moveToNext()){
                return readRouteFromCursor(cursor);
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Route> getAllRoutes(){
        List<Route> routes = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Routes.TABLE_NAME, null)){
            while(cursor.moveToNext()){
                routes.add(readRouteFromCursor(cursor));
            }
        }
        return routes;
    }
    public List<Route> searchRoutesByNumber(String number) {
        List<Route> routes = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + DatabaseContract.Routes.TABLE_NAME +
                            " WHERE " + DatabaseContract.Routes.COL_NAME + " LIKE ?",
                    new String[]{"%" + number + "%"}
            );

            if (cursor.moveToFirst()) {
                do {
                    routes.add(readRouteFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("RouteDao", "Error in searchRoutesByNumber", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return routes;
    }

    public List<Route> searchRoutesByPoints(String from, String to) {
        List<Route> routes = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + DatabaseContract.Routes.TABLE_NAME +
                            " WHERE " + DatabaseContract.Routes.COL_ORIGIN + " LIKE ? AND " +
                            DatabaseContract.Routes.COL_DESTINATION + " LIKE ?",
                    new String[]{"%" + from + "%", "%" + to + "%"}
            );

            if (cursor.moveToFirst()) {
                do {
                    routes.add(readRouteFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("RouteDao", "Error in searchRoutesByPoints", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return routes;
    }
    private Route readRouteFromCursor(Cursor cursor) {
        Route route = new Route();
        route.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Routes.COL_ID)));
        route.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Routes.COL_NAME)));
        route.setOrigin(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Routes.COL_ORIGIN)));
        route.setDestination(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Routes.COL_DESTINATION)));
        route.setStopsJson(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Routes.COL_STOPS_JSON)));
        return route;
    }
}
