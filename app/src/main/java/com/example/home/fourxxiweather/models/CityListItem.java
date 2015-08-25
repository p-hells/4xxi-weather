package com.example.home.fourxxiweather.models;

public class CityListItem {

    String city;
    String country;
    String temperature;
    int weatherPicId;

    public CityListItem(String city, String country, String temperature, int weatherPicId) {
        this.city = city;
        this.country = country;
        this.temperature = temperature;
        this.weatherPicId = weatherPicId;
    }

    public String getCity() {
        return  city;
    }

    public String getCountry() {
        return country;
    }

    public String getTemperature() {
        return temperature;
    }

    public int getWeatherPicId() {
        return weatherPicId;
    }

}
