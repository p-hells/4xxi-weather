package com.example.home.fourxxiweather.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.consts.Consts;
import com.google.android.gms.location.places.Place;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommonMethods {

    public static Address getAddress(Context context, Place place, Locale locale) {
        Address result = null;
        Geocoder geocoder = locale == null ? new Geocoder(context) : new Geocoder(context, locale);
        try {
            List<Address> adresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
            if (adresses != null && !adresses.isEmpty()) {
                result = adresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getDayOfWeek(Context context, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String dayOfWeek = sdf.format(date);
        if (dayOfWeek.equals(context.getString(R.string.week_monday_lowercase))) {
            dayOfWeek = context.getString(R.string.week_monday_uppercase);
        } else if (dayOfWeek.equals(context.getString(R.string.week_tuesday_lowercase))) {
            dayOfWeek = context.getString(R.string.week_tuesday_uppercase);
        } else if (dayOfWeek.equals(context.getString(R.string.week_wednesday_lowercase))) {
            dayOfWeek = context.getString(R.string.week_wednesday_uppercase);
        } else if (dayOfWeek.equals(context.getString(R.string.week_thursday_lowercase))){
            dayOfWeek = context.getString(R.string.week_thursday_uppercase);
        } else if (dayOfWeek.equals(context.getString(R.string.week_friday_lowercase))){
            dayOfWeek = context.getString(R.string.week_friday_uppercase);
        } else if (dayOfWeek.equals(context.getString(R.string.week_saturday_lowercase))){
            dayOfWeek = context.getString(R.string.week_saturday_uppercase);
        } else if (dayOfWeek.equals(context.getString(R.string.week_sunday_lowercase))){
            dayOfWeek = context.getString(R.string.week_sunday_uppercase);
        }
        return dayOfWeek;
    }

    public static String getTemperatureString(double temp) {
        temp += Consts.KELVIN;
        String result = temp > 0 ? "+" : "-";
        DecimalFormat df = new DecimalFormat("#");
        result += df.format(temp);
        return result;
    }

    public static String getWeatherRu(Context context, String weather){
        String result = weather;
        if (result.equals(context.getString(R.string.weather_clear))) {
            result = context.getString(R.string.weather_clear_ru);
        } else if (result.equals(context.getString(R.string.weather_thunderstorm))) {
            result = context.getString(R.string.weather_thunderstorm_ru);
        } else if (result.equals(context.getString(R.string.weather_drizzle))) {
            result = context.getString(R.string.weather_drizzle_ru);
        } else if (result.equals(context.getString(R.string.weather_rain))) {
            result = context.getString(R.string.weather_rain_ru);
        } else if (result.equals(context.getString(R.string.weather_snow))) {
            result = context.getString(R.string.weather_snow_ru);
        } else if (result.equals(context.getString(R.string.weather_clouds))) {
            result = context.getString(R.string.weather_clouds_ru);
        }
        return result;
    }

    public static int getSmallWhiteWeatherImageId(Context context, String weather) {
        int result = R.drawable.ic_cloud_white_24dp;
        if (weather.equals(context.getString(R.string.weather_clear))) {
            result =  R.drawable.ic_white_balance_sunny_white_24dp;
        } else if (weather.equals(context.getString(R.string.weather_thunderstorm))) {
            result =  R.drawable.ic_weather_lightning_white_24dp;
        } else if (weather.equals(context.getString(R.string.weather_drizzle))) {
            result =  R.drawable.ic_weather_pouring_white_24dp;
        } else if (weather.equals(context.getString(R.string.weather_rain))) {
            result =  R.drawable.ic_weather_pouring_white_24dp;
        } else if (weather.equals(context.getString(R.string.weather_snow))) {
            result =  R.drawable.ic_weather_snowy_white_24dp;
        } else if (weather.equals(context.getString(R.string.weather_clouds))) {
            result =  R.drawable.ic_cloud_white_24dp;
        }
        return result;
    }

    public static int getSmallGreyWeatherImageId(Context context, String weather) {
        int result = R.drawable.ic_cloud_grey600_24dp;
        if (weather.equals(context.getString(R.string.weather_clear))) {
            result =  R.drawable.ic_white_balance_sunny_grey600_24dp;
        } else if (weather.equals(context.getString(R.string.weather_thunderstorm))) {
            result =  R.drawable.ic_weather_lightning_grey600_24dp;
        } else if (weather.equals(context.getString(R.string.weather_drizzle))) {
            result =  R.drawable.ic_weather_pouring_grey600_24dp;
        } else if (weather.equals(context.getString(R.string.weather_rain))) {
            result =  R.drawable.ic_weather_pouring_grey600_24dp;
        } else if (weather.equals(context.getString(R.string.weather_snow))) {
            result =  R.drawable.ic_weather_snowy_grey600_24dp;
        } else if (weather.equals(context.getString(R.string.weather_clouds))) {
            result =  R.drawable.ic_cloud_grey600_24dp;
        }
        return result;
    }

    public static int getBigWeatherImageId(Context context, String weather) {
        int result = R.drawable.ic_cloud_grey_96dp;
        if (weather.equals(context.getString(R.string.weather_clear))) {
            result =  R.drawable.ic_white_balance_sunny_grey_96dp;
        } else if (weather.equals(context.getString(R.string.weather_thunderstorm))) {
            result =  R.drawable.ic_weather_lightning_grey_96dp;
        } else if (weather.equals(context.getString(R.string.weather_drizzle))) {
            result =  R.drawable.ic_weather_pouring_grey_96dp;
        } else if (weather.equals(context.getString(R.string.weather_rain))) {
            result =  R.drawable.ic_weather_pouring_grey_96dp;
        } else if (weather.equals(context.getString(R.string.weather_snow))) {
            result =  R.drawable.ic_weather_snowy_grey_96dp;
        } else if (weather.equals(context.getString(R.string.weather_clouds))) {
            result =  R.drawable.ic_cloud_grey_96dp;
        }
        return result;
    }

}
