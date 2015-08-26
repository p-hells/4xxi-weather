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

    public static int getRuCitiesIdsCount(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        return sPref.getInt(context.getString(R.string.pref_ru_cities_ids_count), 0);
    }

    public static void incrementRuCitiesIdsCount(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        int count = sPref.getInt(context.getString(R.string.pref_ru_cities_ids_count), 0);
        count++;
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(context.getString(R.string.pref_ru_cities_ids_count), count);
        ed.commit();
    }

    //0 - not requested
    //1 - loading
    //2 - fully loaded
    public static int getIdLoadingState(Context context){
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        return sPref.getInt(context.getString(R.string.pref_id_loading_state), 0);
    }

    public static void setIdLoadingState(Context context, int state) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(context.getString(R.string.pref_id_loading_state), state);
        ed.commit();
    }

}
