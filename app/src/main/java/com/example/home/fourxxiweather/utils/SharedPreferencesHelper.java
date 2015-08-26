package com.example.home.fourxxiweather.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.models.City;

public class SharedPreferencesHelper {

    public static City getChoosenCity(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        String name = sPref.getString(context.getString(R.string.pref_city_name), "");
        String country = sPref.getString(context.getString(R.string.pref_country_name), "");
        return new City(name, country);
    }

    public static void setChoosenCity(Context context, City city) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(context.getString(R.string.pref_city_name), city.getName());
        ed.putString(context.getString(R.string.pref_country_name), city.getCountry());
        ed.commit();
    }

}
