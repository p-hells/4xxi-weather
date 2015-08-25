package com.example.home.fourxxiweather.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.home.fourxxiweather.activities.MainActivity;
import com.example.home.fourxxiweather.models.ApiWeather;
import com.example.home.fourxxiweather.models.CityInt;

public class AsyncTaskAccumulator {

    public static class GetWeatherTask extends AsyncTask<Void, Void, Boolean> {

        private final Context context;
        private CityInt cityInt;
        private String dCount;
        private ApiWeather weather;

        public GetWeatherTask(Context context, CityInt cityInt, String dCount) {
            this.context = context;
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
            return (weather != null && weather.getList() != null && weather.getCity() != null);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                ((MainActivity) context).setWeather(weather);
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

}
