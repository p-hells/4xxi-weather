package com.example.home.fourxxiweather.adapters;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class OpenWeatherRestAdapter {

    public static RestAdapter getRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org/data/2.5")
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        throw new RuntimeException("Network exception");
                    }
                })
                .build();
    }

}
