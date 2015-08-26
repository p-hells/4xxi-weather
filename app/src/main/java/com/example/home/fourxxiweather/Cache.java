package com.example.home.fourxxiweather;

import com.example.home.fourxxiweather.models.ApiWeather;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cache {

    public static Map<String, ApiWeather> WeatherCacheDictionary = new HashMap<>();
    public static ArrayList<JSONObject> JsonList = new ArrayList<>();

}
