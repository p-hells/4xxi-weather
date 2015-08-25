package com.example.home.fourxxiweather.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.location.places.Place;

import java.io.IOException;
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

}
