package com.example.home.fourxxiweather.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.home.fourxxiweather.consts.DBPrefs;
import com.example.home.fourxxiweather.models.ApiDay;
import com.example.home.fourxxiweather.models.ApiWeather;
import com.example.home.fourxxiweather.models.City;
import com.example.home.fourxxiweather.models.CityInt;
import com.example.home.fourxxiweather.models.CityListItem;
import com.example.home.fourxxiweather.models.ExtendedWeatherItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        db.execSQL("create table " + DBPrefs.TABLE_EXTENDED_WEATHER + " ("
                + DBPrefs.COL_ID + " integer primary key autoincrement,"
                + DBPrefs.COL_CITY_NAME + " text,"
                + DBPrefs.COL_COUNTRY + " text,"
                + DBPrefs.COL_DATE + " text,"
                + DBPrefs.COL_WEATHER + " text,"
                + DBPrefs.COL_TEMPERATURE + " text" + ");");
        db.execSQL("create table " + DBPrefs.TABLE_CITIES_ID + " ("
                + DBPrefs.COL_ID + " integer primary key autoincrement,"
                + DBPrefs.COL_CITY_ID + " text,"
                + DBPrefs.COL_CITY_NAME_INT + " text,"
                + DBPrefs.COL_COUNTRY_CODE + " text,"
                + DBPrefs.COL_COORD + " text" + ");");
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
        ArrayList<City> cities = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = new String[]{DBPrefs.COL_CITY_NAME, DBPrefs.COL_COUNTRY};
        Cursor cursor = db.query(DBPrefs.TABLE_TRACKED_CITIES, columns, null, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            boolean exit = false;
            while (!exit) {
                String city = cursor.getString(cursor.getColumnIndex(columns[0]));
                String country = cursor.getString(cursor.getColumnIndex(columns[1]));
                cities.add(new City(city, country));
                if (!cursor.isLast()) {
                    cursor.moveToNext();
                } else {
                    exit = true;
                }
            }
        }
        for (int i = 0; i < cities.size(); i++) {
            String city = cities.get(i).getName();
            String country = cities.get(i).getCountry();
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
            String date = dateFormat.format(System.currentTimeMillis());
            String selection = DBPrefs.COL_CITY_NAME + " = ?" + " and " + DBPrefs.COL_COUNTRY +
                    " = ?" + " and " + DBPrefs.COL_DATE + " = ?";
            String[] selectionArgs = new String[]{city, country, date};
            cursor = db.query(DBPrefs.TABLE_EXTENDED_WEATHER, null, selection, selectionArgs, null, null, null);
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                String temperature = cursor.getString(cursor.getColumnIndex(DBPrefs.COL_TEMPERATURE));
                String weather = cursor.getString(cursor.getColumnIndex(DBPrefs.COL_WEATHER));
                int weatherpicId = CommonMethods.getSmallWhiteWeatherImageId(context, weather);
                result.add(new CityListItem(city, country, temperature, weatherpicId));
            } else {
                result.add(new CityListItem(city, country, "?", 0));
            }
            cursor.close();
        }
        this.close();
        return result;
    }

    public CityInt getCityInt(City city) {
        String cityName = city.getName();
        String country = city.getCountry();
        String name = null;
        String countryCode = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBPrefs.COL_CITY_NAME + " = ?" + " and " + DBPrefs.COL_COUNTRY + " = ?";
        String[] selectionArgs = new String[]{cityName, country};
        Cursor cursor = db.query(DBPrefs.TABLE_TRACKED_CITIES, null, selection, selectionArgs, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(DBPrefs.COL_CITY_NAME_INT));
            countryCode = cursor.getString(cursor.getColumnIndex(DBPrefs.COL_COUNTRY_CODE));
        }
        cursor.close();
        this.close();
        return new CityInt(name, countryCode);
    }

    public void fillWeatherTable(ApiWeather weather, City city) {
        String name = city.getName();
        String country = city.getCountry();
        SQLiteDatabase db = this.getWritableDatabase();
        List<ApiDay> days = weather.getList();
        for (int i = 0; i < days.size(); i++) {
            ApiDay day = days.get(i);
            long dateTime = day.getDt() * 1000;
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
            String date = (dateFormat.format(dateTime));
            String selection = DBPrefs.COL_CITY_NAME + " = ?" + " and " + DBPrefs.COL_COUNTRY +
                    " = ?" + " and " + DBPrefs.COL_DATE + " = ?";
            String[] selectionArgs = new String[]{name, country, date};
            Cursor cursor = db.query(DBPrefs.TABLE_EXTENDED_WEATHER, null, selection, selectionArgs, null, null, null);
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                int id = cursor.getInt(cursor.getColumnIndex(DBPrefs.COL_ID));
                db.delete(DBPrefs.TABLE_EXTENDED_WEATHER, "id = " + id, null);
            }
            String weatherDescription = day.getWeather().get(0).getWeather();
            String temperature = CommonMethods.getTemperatureString(day.getTemp().getTemperature());
            ContentValues cv = new ContentValues();
            cv.put(DBPrefs.COL_CITY_NAME, name);
            cv.put(DBPrefs.COL_COUNTRY, country);
            cv.put(DBPrefs.COL_DATE, date);
            cv.put(DBPrefs.COL_WEATHER, weatherDescription);
            cv.put(DBPrefs.COL_TEMPERATURE, temperature);
            db.insert(DBPrefs.TABLE_EXTENDED_WEATHER, null, cv);
        }
    }

    public ArrayList<ExtendedWeatherItem> getExtendedWeatherList(City city, int dayCount) {
        ArrayList<ExtendedWeatherItem> result = new ArrayList<>();
        String cityName = city.getName();
        String country = city.getCountry();
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBPrefs.COL_CITY_NAME + " = ?" + " and " + DBPrefs.COL_COUNTRY + " = ?";
        String[] selectionArgs = new String[]{cityName, country};
        Cursor cursor = db.query(DBPrefs.TABLE_EXTENDED_WEATHER, null, selection, selectionArgs, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            boolean exit = false;
            boolean success = false;
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
            int i = 0;
            long currentDate = System.currentTimeMillis();
            while (!(exit || success)) {
                String dateString = cursor.getString(cursor.getColumnIndex(DBPrefs.COL_DATE));
                Date date = null;
                try {
                    date = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date != null && date.getTime() > currentDate) {
                    i++;
                    currentDate = date.getTime();
                    String dayOfWeek = CommonMethods.getDayOfWeek(context, date);
                    String temperature = cursor.getString(cursor.getColumnIndex(DBPrefs.COL_TEMPERATURE));
                    String weather = cursor.getString(cursor.getColumnIndex(DBPrefs.COL_WEATHER));
                    int weatherPicId = CommonMethods.getSmallGreyWeatherImageId(context, weather);
                    result.add(new ExtendedWeatherItem(dateString, dayOfWeek, temperature, weatherPicId));
                }
                success = i >= dayCount;
                if (!cursor.isLast()) {
                    cursor.moveToNext();
                } else {
                    exit = true;
                }
            }
        }
        cursor.close();
        this.close();
        return result;
    }

    public String getCityId(CityInt cityInt) {
        String result = null;
        String name = cityInt.getName();
        String countryCode = cityInt.getCountryCode();
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBPrefs.COL_CITY_NAME_INT + " = ?" + " and " + DBPrefs.COL_COUNTRY_CODE + " = ?";
        String[] selectionArgs = new String[]{name, countryCode};
        Cursor cursor = db.query(DBPrefs.TABLE_CITIES_ID, null, selection, selectionArgs, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex(DBPrefs.COL_CITY_ID));
        }
        cursor.close();
        this.close();
        return result;
    }

}
