package com.example.home.fourxxiweather.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.home.fourxxiweather.Cache;
import com.example.home.fourxxiweather.activities.MainActivity;
import com.example.home.fourxxiweather.activities.SplashScreenActivity;
import com.example.home.fourxxiweather.models.ApiWeather;
import com.example.home.fourxxiweather.models.City;
import com.example.home.fourxxiweather.models.CityInt;

public class AsyncTaskAccumulator {

    public static class GetWeatherTask extends AsyncTask<Void, Void, Boolean> {

        private final Context context;
        private City city;
        private CityInt cityInt;
        private String dCount;
        private ApiWeather weather;

        public GetWeatherTask(Context context, City city, CityInt cityInt, String dCount) {
            this.context = context;
            this.city = city;
            this.cityInt = cityInt;
            this.dCount = dCount;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            RequestSender sender = new RequestSender(context);
            try {
                weather = sender.getWeather(cityInt.getName() + "," + cityInt.getCountryCode(), dCount);
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

}
