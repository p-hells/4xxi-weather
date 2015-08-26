package com.example.home.fourxxiweather.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.home.fourxxiweather.Cache;
import com.example.home.fourxxiweather.activities.MainActivity;
import com.example.home.fourxxiweather.activities.SplashScreenActivity;
import com.example.home.fourxxiweather.consts.Consts;
import com.example.home.fourxxiweather.consts.DBPrefs;
import com.example.home.fourxxiweather.models.ApiWeather;
import com.example.home.fourxxiweather.models.City;
import com.example.home.fourxxiweather.models.CityInt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncTaskAccumulator {

    public static class GetWeatherTask extends AsyncTask<Void, Void, Boolean> {

        private final Context context;
        private City city;
        private CityInt cityInt;
        private String id;
        private String dCount;
        private ApiWeather weather;

        public GetWeatherTask(Context context, City city, CityInt cityInt, String id, String dCount) {
            this.context = context;
            this.city = city;
            this.cityInt = cityInt;
            this.id = id;
            this.dCount = dCount;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            RequestSender sender = new RequestSender(context);
            try {
                if (id != null && !id.isEmpty()) {
                    weather = sender.getWeatherById(id, dCount);
                } else {
                    weather = sender.getWeatherByCity(cityInt.getName() + "," + cityInt.getCountryCode(), dCount);
                }
            } catch (RuntimeException error) {
                return false;
            }
            if (weather != null && weather.getList() != null && weather.getCity() != null) {
                Cache.WeatherCacheDictionary.put(city.getName() + city.getCountry(), weather);
                DBHelper dbHelper = new DBHelper(context);
                dbHelper.fillWeatherTable(weather, city);
            }

            return (weather != null && weather.getList() != null && weather.getCity() != null);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (context instanceof MainActivity) {
                if (success) {
                    ((MainActivity) context).setWeather(weather);
                } else {
                    ((MainActivity) context).setNoData();
                }
            } else {
                ((SplashScreenActivity) context).processResult();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    public static class EmptyTask extends AsyncTask<Void, Void, Boolean> {

        Context context;

        public EmptyTask(Context context) {
            this.context = context;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            ((SplashScreenActivity) context).finish();
        }
    }

    public static class ExtractJsonTask extends AsyncTask<Void, Void, Boolean> {

        private final Context context;
        final static String myLog = "myLog";

        public ExtractJsonTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(myLog, "JSON EXTRACTION STARTED");
            Cache.JsonList.clear();
            JSONObject obj;
            try {
                InputStream is = context.getApplicationContext().getAssets().open("citiesRU.txt");
                InputStreamReader inputReader = new InputStreamReader(is, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputReader);
                String line = buffreader.readLine();
                while (line != null) {
                    if (isCancelled()) {
                        buffreader.close();
                        return false;
                    }
                    obj = new JSONObject(line);
                    Cache.JsonList.add(obj);
                    line = buffreader.readLine();
                }
                buffreader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(myLog, "JSON EXTRACTION COMPLETE ");
            } else {
                Log.d(myLog, "JSON EXTRACTION FAILED ");
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(myLog, "JSON EXTRACTION CANCEL ");
        }

    }

    public static class FillRuCitiesIdsTask extends AsyncTask<Void, Integer, Boolean> {

        private final Context context;
        final static String myLog = "myLog";


        public FillRuCitiesIdsTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(myLog, "CITIES IDS LOADING STARTED");
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            for (int i = 0; i < Cache.JsonList.size(); i++) {
                if (isCancelled()) {
                    dbHelper.close();
                    return null;
                }
                JSONObject json = Cache.JsonList.get(i);
                try {
                    String cityId = json.getString("_id");
                    String cityName = json.getString("name");
                    String country = json.getString("country");
                    String coord = json.getString("coord");
                    String selection = DBPrefs.COL_CITY_ID + " = ?";
                    String[] selectionArgs = new String[]{cityId};
                    Cursor cursor = db.query(DBPrefs.TABLE_CITIES_ID, null, selection, selectionArgs, null, null, null);
                    if (cursor.getCount() == 0) {
                        ContentValues cv = new ContentValues();
                        cv.put(DBPrefs.COL_CITY_ID, cityId);
                        cv.put(DBPrefs.COL_CITY_NAME_INT, cityName);
                        cv.put(DBPrefs.COL_COUNTRY_CODE, country);
                        cv.put(DBPrefs.COL_COORD, coord);
                        db.insert(DBPrefs.TABLE_CITIES_ID, null, cv);
                        SharedPreferencesHelper.incrementRuCitiesIdsCount(context);
                        int count = SharedPreferencesHelper.getRuCitiesIdsCount(context);

                        if (count % 10 == 0 || count == Consts.RU_CITIES_COUNT) {
                            publishProgress(count);
                        }

                        Log.d(myLog, "INSERTED RECORDS:" + SharedPreferencesHelper.getRuCitiesIdsCount(context));
                    }
                    cursor.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            dbHelper.close();
            int count = SharedPreferencesHelper.getRuCitiesIdsCount(context);
            return count == Consts.RU_CITIES_COUNT;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ((MainActivity) context).setProgressViewValue(values[0]);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(myLog, "CITIES IDS FULLY LOADED ");
                SharedPreferencesHelper.setIdLoadingState(context, 2);
                ((MainActivity) context).setProgressViewValue(Consts.RU_CITIES_COUNT);
            } else {
                Log.d(myLog, "CITIES IDS LOADING FAILED ");
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(myLog, "CITIES IDS LOADING CANCEL ");
        }

    }


}
