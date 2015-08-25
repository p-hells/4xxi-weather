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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.adapters.CityListAdapter;
import com.example.home.fourxxiweather.consts.RequestCodes;
import com.example.home.fourxxiweather.models.CityListItem;
import com.example.home.fourxxiweather.utils.CommonMethods;
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
    boolean firstStartCheck;

    //UI references
    ListView lvCities;
    TextView tvCity;
    TextView tvTemperature;
    Button btnAddaCity;
    PopupMenu popupMenu;


    private final int LOWER_BOUND_LATITUDE = 45;
    private final int LOWER_BOUND_LONGITUDE = 10;
    private final int UPPER_BOUND_LATITUDE = 55;
    private final int UPPER_BOUND_LONGITUDE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cities = new ArrayList<>();
        firstStartCheck = true;
        lvCities = (ListView) findViewById(R.id.lvCities);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        btnAddaCity = (Button) findViewById(R.id.btnAddCity);
        popupMenu = new PopupMenu(this, btnAddaCity);
        popupMenu.getMenu().add(1, 1, 1, getString(R.string.popup_item_show_on_map));
        popupMenu.getMenu().add(1, 2, 1, getString(R.string.popup_item_enter_manually));
        setEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstStartCheck) {
            firstStartCheck = false;
            cities.add(new CityListItem(getString(R.string.city_Moscow),
                    getString(R.string.country_Russia), "+ 20 C", 0));
            cities.add(new CityListItem(getString(R.string.city_St_Petersburg),
                    getString(R.string.country_Russia), "+ 25 C", 0));
        }
        fillCityListView();
    }

    private void fillCityListView() {
        CityListAdapter adapter = new CityListAdapter(this, cities);
        lvCities.setAdapter(adapter);
    }

    private void setEvents() {
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
                popupMenu.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                processPopupItemClick(item.getItemId());
                return false;
            }
        });
    }

    private void processNewCity(String city, String temperature) {
        tvCity.setText(city);
        tvTemperature.setText(temperature);
    }

    private void processPopupItemClick(int id) {
        if (id == 1) {
            showCityOnMap();
        } else {
            enterCityNameManually();
        }
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

    private void enterCityNameManually() {
        Intent intent = new Intent(this, CityFinderActivity.class);
        startActivityForResult(intent, RequestCodes.CITY_FINDER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String cityName = null;
            String country = null;
            String cityNameInt = null;
            String countryCode = null;
            if (requestCode == RequestCodes.PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, this);
                Address address = CommonMethods.getAddress(this, place, null);
                Address addressInt = CommonMethods.getAddress(this, place, Locale.ENGLISH);
                if (address != null && address.getLocality() != null && address.getCountryName() != null &&
                        address.getCountryCode() != null && addressInt != null && addressInt.getLocality() != null) {
                    cityName = address.getLocality();
                    country = address.getCountryName();
                    cityNameInt = addressInt.getLocality();
                    countryCode = address.getCountryCode();
                }
            } else if (requestCode == RequestCodes.CITY_FINDER_REQUEST) {
                cityName = data.getStringExtra("City");
                country = data.getStringExtra("Country");
                cityNameInt = data.getStringExtra("CityInt");
                countryCode = data.getStringExtra("CountryCode");
            }
            if (cityName != null) {
                String toastMsg = String.format(getString(R.string.txt_choosen_place) + " %s, %s",
                        cityName, country);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                cities.add(new CityListItem(cityName, country, "?", 0));
                fillCityListView();
            } else {
                Toast.makeText(this, getString(R.string.txt_empty_place), Toast.LENGTH_LONG).show();
            }
        }
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
