package com.example.home.fourxxiweather.models;

public class ExtendedWeatherItem {

    String date;
    String dayOfWeek;
    String temperature;
    int weatherImageId;

    public ExtendedWeatherItem(String date, String dayOfWeek, String temperature, int weatherImageId) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.temperature = temperature;
        this.weatherImageId = weatherImageId;
    }

    public String getDate() {
        return date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTemperature() {
        return temperature;
    }

    public int getWeatherImageId() {
        return weatherImageId;
    }

}
