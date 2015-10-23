package com.davidbarron.weather;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseDriver extends SQLiteOpenHelper {
    private SQLiteDatabase diaryData;
    private static final String tableName = "locations";

    public DatabaseDriver(Context context) {
        super(context, "weather.db", null, 1);
        open();

    }

    public void open() {
        diaryData = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        diaryData = db;
        diaryData.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (_id integer primary key autoincrement, city text not null, state date not null, home boolean);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(DatabaseDriver.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
        onCreate(db);
    }

    public void insertLocation(String city, String state, boolean home_boolean) {
        Log.i(DatabaseDriver.class.getName(), "Inserting row.");
        ContentValues values = new ContentValues();
        values.put("city", city);
        values.put("state", state);
        values.put("home", home_boolean);
        long rowid = diaryData.insert(tableName, null, values);
        Log.i(DatabaseDriver.class.getName(), "Row inserted, id: " + rowid);
    }

    public void deleteLocation(long id) {
        diaryData.delete(tableName,"_id=" + id,null);
    }

    public Cursor getLocation(long id) {
        if (!diaryData.isOpen())
            diaryData = getWritableDatabase();
        String[] columns = {"_id", "city", "state"};
        String where = "_id=" + id;
        Cursor c = diaryData.query(tableName, columns, where, null, null, null, null, null);
        return c;
    }

    public Cursor getAllLocations() {
        String[] columns = {"_id", "city", "state"};
        Cursor c = diaryData.query(tableName, columns, null, null, null, null, null, null);
        return c;
    }
    public Cursor getHomeLocation() {
        String[] columns = {"_id", "city", "state"};
        Cursor c = diaryData.query(tableName, columns,"home=1",null,null,null,null);
        return c;
    }
}
