package com.example.evgeniy.test.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.evgeniy.test.Objects.Data;

public class DBHelper extends SQLiteOpenHelper {

    private String LOG_TAG = "DBHelper";
    public static final int DBVersion = 3;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE city (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "name text," +
                "tempratute text," +
                "feels_like text," +
                "wind_speed text," +
                "wind_dir text," +
                "pressure_mm text," +
                "humidity text," +
                "cloudness text," +
                "condition text," +
                "daytime text" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS city");
        onCreate(db);
    }

    public Data getWeatherFromBD(SQLiteDatabase db, String City) {
        Log.d(LOG_TAG, "getWeatherFromBD: " + City);
        Data result = new Data();
        Cursor city = db.rawQuery("SELECT * FROM city c WHERE c.name=?", new String[]{City});
        city.moveToLast();
        if (city.getCount() != 0) {
            result.fullName = city.getString(city.getColumnIndex("name"));
            result.temp = city.getString(city.getColumnIndex("tempratute"));
            result.feels_like = city.getString(city.getColumnIndex("feels_like"));
            result.wind_speed = city.getString(city.getColumnIndex("wind_speed"));
            result.wind_dir = city.getString(city.getColumnIndex("wind_dir"));
            result.pressure_mm = city.getString(city.getColumnIndex("pressure_mm"));
            result.humidity = city.getString(city.getColumnIndex("humidity"));
            result.Cloudnes = city.getString(city.getColumnIndex("cloudness"));
            result.condition = city.getString(city.getColumnIndex("condition"));
            result.daytime = city.getString(city.getColumnIndex("daytime"));
            city.close();
            return result;
        }
        city.close();
        return null;
    }

    public long setWeatherToBD(SQLiteDatabase db, Data City) {
        ContentValues newValues = new ContentValues();
        newValues.put("name", City.fullName);
        newValues.put("tempratute", City.temp);
        newValues.put("feels_like", City.feels_like);
        newValues.put("wind_speed", City.wind_speed);
        newValues.put("wind_dir", City.wind_dir);
        newValues.put("pressure_mm", City.pressure_mm);
        newValues.put("humidity", City.humidity);
        newValues.put("cloudness", City.Cloudnes);
        newValues.put("condition", City.condition);
        newValues.put("daytime", City.daytime);
        return db.insert("city", null, newValues);
    }

}
