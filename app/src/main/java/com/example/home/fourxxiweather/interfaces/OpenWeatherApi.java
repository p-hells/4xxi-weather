package com.example.home.fourxxiweather.interfaces;

import com.example.home.fourxxiweather.models.ApiWeather;

import retrofit.http.GET;
import retrofit.http.Query;

public interface OpenWeatherApi {

    @GET("/forecast/daily")
    ApiWeather GetWeatherByCityName(@Query("q") String cityName, @Query("cnt") String dCount);

}
