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

import com.example.home.fourxxiweather.Cache;
import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.adapters.CityListAdapter;
import com.example.home.fourxxiweather.adapters.ExtendedWeatherListAdapter;
import com.example.home.fourxxiweather.consts.Consts;
import com.example.home.fourxxiweather.consts.RequestCodes;
import com.example.home.fourxxiweather.models.ApiDay;
import com.example.home.fourxxiweather.models.ApiWeather;
import com.example.home.fourxxiweather.models.ApiWeatherDescriptor;
import com.example.home.fourxxiweather.models.City;
import com.example.home.fourxxiweather.models.CityInt;
import com.example.home.fourxxiweather.models.CityListItem;
import com.example.home.fourxxiweather.models.ExtendedWeatherItem;
import com.example.home.fourxxiweather.utils.AsyncTaskAccumulator;
import com.example.home.fourxxiweather.utils.CommonMethods;
import com.example.home.fourxxiweather.utils.DBHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    boolean firstStartCheck;
    private AsyncTaskAccumulator.GetWeatherTask getWeatherTask;
    City choosenCity;


    //UI references
    ListView lvCities;
    ListView lvExtendedWeather;
    TextView tvCity;
    TextView tvTemperature;
    TextView tvDate;
    TextView tvDayOfWeek;
    TextView tvWeather;
    TextView tvWind;
    TextView tvPressure;
    TextView tvCloudy;
    TextView tvHumidity;
    Button btnThree;
    Button btnWeek;
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
        dbHelper = new DBHelper(this);
        firstStartCheck = true;
        lvCities = (ListView) findViewById(R.id.lvCities);
        lvExtendedWeather = (ListView) findViewById(R.id.lvExtendedWeather);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDayOfWeek = (TextView) findViewById(R.id.tvDayOfWeek);
        tvWeather = (TextView) findViewById(R.id.tvWeather);
        tvWind = (TextView) findViewById(R.id.tvWind);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvPressure = (TextView) findViewById(R.id.tvPressure);
        tvCloudy = (TextView) findViewById(R.id.tvCloudy);
        btnAddaCity = (Button) findViewById(R.id.btnAddCity);
        btnThree = (Button) findViewById(R.id.btnThree);
        btnWeek = (Button) findViewById(R.id.btnWeek);

        popupMenu = new PopupMenu(this, btnAddaCity);
        popupMenu.getMenu().add(1, 1, 1, getString(R.string.popup_item_show_on_map));
        popupMenu.getMenu().add(1, 2, 1, getString(R.string.popup_item_enter_manually));
        setEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstStartCheck) {
            firstStartCheck = false;
            dbHelper.addRecordToCityTable(new City(getString(R.string.city_Moscow), getString(R.string.country_Russia)),
                    new CityInt(getString(R.string.city_Moscow_int), getString(R.string.country_Russia_code)));
            dbHelper.addRecordToCityTable(new City(getString(R.string.city_St_Petersburg), getString(R.string.country_Russia)),
                    new CityInt(getString(R.string.city_St_Petersburg_int), getString(R.string.country_Russia_code)));
        }
        fillCityListView();
    }

    private void fillCityListView() {
        ArrayList<CityListItem> cities = dbHelper.getCitiesList();
        CityListAdapter adapter = new CityListAdapter(this, cities);
        lvCities.setAdapter(adapter);
    }

    private void setEvents() {
        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityListItem item = (CityListItem) parent.getItemAtPosition(position);
                processNewCity(new City(item.getCity(), item.getCountry()));
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

        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processExtendedWeather(v);
            }
        });

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processExtendedWeather(v);
            }
        });
    }

    private void processNewCity(City city) {
        choosenCity = city;
        ApiWeather weather = loadFromCache(city);
        if (weather != null) {
            setWeather(weather);
        } else {
            CityInt cityInt = dbHelper.getCityInt(city);
            getWeatherTask = new AsyncTaskAccumulator.GetWeatherTask(this, city, cityInt, Consts.DAYS);
            getWeatherTask.execute((Void) null);
        }
    }

    private ApiWeather loadFromCache(City city) {
        return Cache.WeatherCacheDictionary.get(city.getName() + city.getCountry());
    }

    private void processPopupItemClick(int id) {
        if (id == 1) {
            showCityOnMap();
        } else {
            enterCityNameManually();
        }
    }

    private void processExtendedWeather(View view) {
        int count = view == btnThree ? 3 : 7;
        ArrayList<ExtendedWeatherItem> extendedWeatherList = dbHelper.getExtendedWeatherList(choosenCity, count);
        ExtendedWeatherListAdapter adapt = new ExtendedWeatherListAdapter(this, extendedWeatherList);
        lvExtendedWeather.setAdapter(adapt);
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
                dbHelper.addRecordToCityTable(new City(cityName, country), new CityInt(cityNameInt, countryCode));
                fillCityListView();
            } else {
                Toast.makeText(this, getString(R.string.txt_empty_place), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setWeather(ApiWeather weather) {
        try {
            String city = weather.getCity().getName();
            tvCity.setText(city);
            ApiDay day = weather.getList().get(0);
            long dateTime = day.getDt() * 1000;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
            tvDate.setText(dateFormat.format(dateTime));
            String dayOfTheWeek = CommonMethods.getDayOfWeek(this, new Date(dateTime));
            tvDayOfWeek.setText(dayOfTheWeek);
            tvTemperature.setText(CommonMethods.getTemperatureString(day.getTemp().getTemperature()));
            ApiWeatherDescriptor descr = day.getWeather().get(0);
            tvWeather.setText(CommonMethods.getWeatherRu(this, descr.getWeather()));
            tvHumidity.setText(String.valueOf(day.getHumidity()));
            DecimalFormat df = new DecimalFormat("#");
            tvPressure.setText(df.format(day.getPressure()));
            df = new DecimalFormat("#.00");
            tvWind.setText(df.format(day.getSpeed()));
            tvCloudy.setText(String.valueOf(day.getClouds()));
            fillCityListView();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}
