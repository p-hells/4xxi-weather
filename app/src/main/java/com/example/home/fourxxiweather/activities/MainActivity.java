package com.example.home.fourxxiweather.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.adapters.CityListAdapter;
import com.example.home.fourxxiweather.consts.RequestCodes;
import com.example.home.fourxxiweather.models.CityListItem;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ArrayList<CityListItem> cities;

    //UI references
    ListView lvCities;
    TextView tvCity;
    TextView tvTemperature;
    Button btnAddaCity;

    private final int LOWER_BOUND_LATITUDE = 45;
    private final int LOWER_BOUND_LONGITUDE = 10;
    private final int UPPER_BOUND_LATITUDE = 55;
    private final int UPPER_BOUND_LONGITUDE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cities = new ArrayList<>();
        lvCities = (ListView) findViewById(R.id.lvCities);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        btnAddaCity = (Button) findViewById(R.id.btnAddCity);
        setEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cities.add(new CityListItem(getString(R.string.city_Moscow),
                getString(R.string.country_Russia), "+ 20 C", 0));
        cities.add(new CityListItem(getString(R.string.city_St_Petersburg),
                getString(R.string.country_Russia), "+ 25 C", 0));

        fillCityListView();
    }

    private void fillCityListView(){
        CityListAdapter adapter = new CityListAdapter(this, cities);
        lvCities.setAdapter(adapter);
    }

    private void setEvents(){
        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityListItem item = (CityListItem) parent.getItemAtPosition(position);
                processNewCity(item.getCity(), item.getTemperature());
            }
        });

        btnAddaCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCityOnMap();
            }
        });
    }

    private void processNewCity(String city, String temperature) {
        tvCity.setText(city);
        tvTemperature.setText(temperature);
    }

    private void showCityOnMap() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(new LatLngBounds(new LatLng(LOWER_BOUND_LATITUDE, LOWER_BOUND_LONGITUDE),
                new LatLng(UPPER_BOUND_LATITUDE, UPPER_BOUND_LONGITUDE)));
        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), RequestCodes.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(data, this);
            Address address = getAddress(this, place, null);
            if (address != null && address.getLocality() != null && address.getCountryName() != null) {
                String city = address.getLocality();
                String country = address.getCountryName();
                cities.add(new CityListItem(city, country, "?", 0));
                String toastMsg = String.format(getString(R.string.txt_choosen_place) + " %s, %s",
                        city, country);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                fillCityListView();
            } else {
                Toast.makeText(this, getString(R.string.txt_empty_place), Toast.LENGTH_LONG).show();
            }
        }
    }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
