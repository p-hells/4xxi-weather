package com.example.home.fourxxiweather.interfaces;

import com.example.home.fourxxiweather.models.ApiWeather;

import retrofit.http.GET;
import retrofit.http.Query;

public interface OpenWeatherApi {

    @GET("/forecast/daily")
    ApiWeather GetWeatherByCityName(@Query("q") String cityName, @Query("cnt") String dCount);

    @GET("/forecast/daily")
    ApiWeather GetWeatherByCityId(@Query("id") String cityId, @Query("cnt") String dCount);

}
