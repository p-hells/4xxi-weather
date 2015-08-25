package com.example.home.fourxxiweather.models;

import java.util.List;

public class ApiWeather {

    ApiCity city;
    int cnt;
    List<ApiDay> list;

    public ApiCity getCity() {
        return city;
    }

    public int getCnt() {
        return cnt;
    }

    public List<ApiDay> getList() {
        return list;
    }
}
