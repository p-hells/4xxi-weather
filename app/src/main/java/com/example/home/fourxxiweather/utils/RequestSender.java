package com.example.home.fourxxiweather.utils;

import android.content.Context;

import com.example.home.fourxxiweather.adapters.OpenWeatherRestAdapter;
import com.example.home.fourxxiweather.interfaces.OpenWeatherApi;
import com.example.home.fourxxiweather.models.ApiWeather;

import retrofit.RestAdapter;

public class RequestSender {

    private RestAdapter restAdapter;
    private Context context;

    public RequestSender(Context context){
        this.context = context;
    }

    public ApiWeather getWeatherById(String cityId, String count){
        restAdapter = OpenWeatherRestAdapter.getRestAdapter();
        OpenWeatherApi api = restAdapter.create(OpenWeatherApi.class);
        return api.GetWeatherByCityId(cityId, count);
    }

    public ApiWeather getWeatherByCity(String cityName, String count){
        restAdapter = OpenWeatherRestAdapter.getRestAdapter();
        OpenWeatherApi api = restAdapter.create(OpenWeatherApi.class);
        return api.GetWeatherByCityName(cityName, count);
    }

}
