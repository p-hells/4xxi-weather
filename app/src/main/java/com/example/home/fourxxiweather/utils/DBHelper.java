package com.example.home.fourxxiweather.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.home.fourxxiweather.consts.DBPrefs;
import com.example.home.fourxxiweather.models.City;
import com.example.home.fourxxiweather.models.CityInt;
import com.example.home.fourxxiweather.models.CityListItem;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private Context context = null;

    public DBHelper(Context context) {
        super(context, DBPrefs.DB_NAME, null, DBPrefs.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DBPrefs.TABLE_TRACKED_CITIES + " ("
                + DBPrefs.COL_ID + " integer primary key autoincrement,"
                + DBPrefs.COL_CITY_NAME + " text,"
                + DBPrefs.COL_CITY_NAME_INT + " text,"
                + DBPrefs.COL_COUNTRY + " text,"
                + DBPrefs.COL_COUNTRY_CODE + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addRecordToCityTable(City city, CityInt cityInt) {
        String cityName = city.getName();
        String countryName = city.getCountry();
        String cityNameInt = cityInt.getName();
        String countryCode = cityInt.getCountryCode();
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBPrefs.COL_CITY_NAME + " = ?" + " and " + DBPrefs.COL_COUNTRY + " = ?";
        String[] selectionArgs = new String[]{cityName, countryName};
        Cursor cursor = db.query(DBPrefs.TABLE_TRACKED_CITIES, null, selection, selectionArgs, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put(DBPrefs.COL_CITY_NAME, cityName);
            cv.put(DBPrefs.COL_CITY_NAME_INT, cityNameInt);
            cv.put(DBPrefs.COL_COUNTRY, countryName);
            cv.put(DBPrefs.COL_COUNTRY_CODE, countryCode);
            db.insert(DBPrefs.TABLE_TRACKED_CITIES, null, cv);
        }
        cursor.close();
        this.close();
    }

    public ArrayList<CityListItem> getCitiesList() {
        ArrayList<CityListItem> result = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = new String[]{DBPrefs.COL_CITY_NAME, DBPrefs.COL_COUNTRY};
        Cursor cursor = db.query(DBPrefs.TABLE_TRACKED_CITIES, columns, null, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            boolean exit = false;
            while (!exit) {
                String city = cursor.getString(cursor.getColumnIndex(columns[0]));
                String country = cursor.getString(cursor.getColumnIndex(columns[1]));
                result.add(new CityListItem(city, country, "?", 0));
                if (!cursor.isLast()) {
                    cursor.moveToNext();
                } else {
                    exit = true;
                }
            }
        }
        return result;
    }

}
