package com.example.home.fourxxiweather.models;

public class ApiWeatherDescriptor {

    int id;
    String main;
    String description;
    String icon;

    public ApiWeatherDescriptor(int id, String main, String description, String icon) {
        this.id = id;
        this.main = main;
        this.description = description;
        this.icon = icon;
    }

    public String getWeather() {
        return main;
    }
}
