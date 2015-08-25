package com.example.home.fourxxiweather.models;

import java.util.List;

public class ApiDay {

    long dt;
    ApiTemperature temp;
    Double pressure;
    int humidity;
    List<ApiWeatherDescriptor> weather;
    Double speed;
    int deg;
    int clouds;

    public long getDt() {
        return dt;
    }

    public ApiTemperature getTemp() {
        return temp;
    }

    public Double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public List<ApiWeatherDescriptor> getWeather() {
        return weather;
    }

    public Double getSpeed() {
        return speed;
    }

    public int getDeg() {
        return deg;
    }

    public int getClouds() {
        return clouds;
    }

}
