package com.example.home.fourxxiweather.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.fourxxiweather.Cache;
import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.adapters.CityListAdapter;
import com.example.home.fourxxiweather.adapters.ExtendedWeatherListAdapter;
import com.example.home.fourxxiweather.consts.Consts;
import com.example.home.fourxxiweather.consts.RequestCodes;
import com.example.home.fourxxiweather.enums.WeatherShowState;
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
import com.example.home.fourxxiweather.utils.SharedPreferencesHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.ProgressView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //UI references
    RelativeLayout rlWeather;
    RelativeLayout rlCities;
    RelativeLayout rlWeatherView;
    RelativeLayout rlIdsLoading;
    LinearLayout llExtendedWeather;
    ListView lvCities;
    ListView lvExtendedWeather;
    TextView tvCity;
    TextView tvDayOfWeek;
    TextView tvDate;
    TextView tvTemperature;
    TextView tvTemperatureMin;
    TextView tvTemperatureMax;
    TextView tvWeather;
    TextView tvWind;
    TextView tvHumidity;
    TextView tvPressure;
    TextView tvClouds;
    TextView tvExtend;
    TextView tvWeatherLoading;
    ProgressView pvWeatherLoading;
    ProgressView pvIdsLoading;
    FloatingActionButton fabWeek;
    FloatingActionButton fabAddCity;
    ImageView ivWeatherMain;
    PopupMenu popupMenu;

    private DBHelper dbHelper;
    private boolean firstStartCheck;
    private WeatherShowState weatherShowState;

    private AsyncTaskAccumulator.GetWeatherTask getWeatherTask;
    private AsyncTaskAccumulator.ExtractJsonTask extractJsonTask;
    private AsyncTaskAccumulator.FillRuCitiesIdsTask fillRuCitiesIdsTask;

    private final int LOWER_BOUND_LATITUDE = 45;
    private final int LOWER_BOUND_LONGITUDE = 10;
    private final int UPPER_BOUND_LATITUDE = 55;
    private final int UPPER_BOUND_LONGITUDE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        rlWeather = (RelativeLayout) findViewById(R.id.rlWeatherView);
        llExtendedWeather = (LinearLayout) findViewById(R.id.llExtendedWeather);
        rlCities = (RelativeLayout) findViewById(R.id.rlCities);
        rlWeatherView = (RelativeLayout) findViewById(R.id.rlWeatherView);
        rlIdsLoading = (RelativeLayout) findViewById(R.id.rlIdsLoading);
        lvCities = (ListView) findViewById(R.id.lvCities);
        lvExtendedWeather = (ListView) findViewById(R.id.lvExtendedWeather);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvDayOfWeek = (TextView) findViewById(R.id.tvDayOfWeek);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvTemperatureMin = (TextView) findViewById(R.id.tvTemperatureMin);
        tvTemperatureMax = (TextView) findViewById(R.id.tvTemperatureMax);
        tvWeather = (TextView) findViewById(R.id.tvWeather);
        tvWind = (TextView) findViewById(R.id.tvWind);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvPressure = (TextView) findViewById(R.id.tvPressure);
        tvClouds = (TextView) findViewById(R.id.tvClouds);
        tvExtend = (TextView) findViewById(R.id.tvExtend);
        tvWeatherLoading = (TextView) findViewById(R.id.tvWeatherLoading);
        pvWeatherLoading = (ProgressView) findViewById(R.id.pvWeatherLoading);
        pvIdsLoading = (ProgressView) findViewById(R.id.pvIdsLoading);
        fabWeek = (FloatingActionButton) findViewById(R.id.fabWeek);
        fabAddCity = (FloatingActionButton) findViewById(R.id.fabAddCity);
        ivWeatherMain = (ImageView) findViewById(R.id.ivWeatherMain);
        popupMenu = new PopupMenu(this, fabAddCity);
        popupMenu.getMenu().add(1, 1, 1, getString(R.string.popup_item_show_on_map));
        popupMenu.getMenu().add(1, 2, 1, getString(R.string.popup_item_enter_manually));
        dbHelper = new DBHelper(this);
        firstStartCheck = true;
        weatherShowState = WeatherShowState.Normal;
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
        if (id == R.id.action_id_loader) {
            Intent intent = new Intent(this, IdLoaderActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search){
            showCityOnMap();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstStartCheck) {
            firstStartCheck = false;
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);
        } else {
            if (SharedPreferencesHelper.getIdLoadingState(this) == 1) {
                rlIdsLoading.setVisibility(View.VISIBLE);
                setProgressViewValue(SharedPreferencesHelper.getRuCitiesIdsCount(this));
            } else {
                rlIdsLoading.setVisibility(View.GONE);
            }
            City city = SharedPreferencesHelper.getChoosenCity(this);
            processNewCity(city);
        }
    }

    @Override
    protected void onStop() {
        cancelPrimaryTask();
        cancelSecondaryTasks();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (weatherShowState == WeatherShowState.Normal) {
            super.onBackPressed();
        } else {
            processExtendedWeather(rlWeather);
        }
    }

    private void fillCityListView() {
        ArrayList<CityListItem> cities = dbHelper.getCitiesList();
        CityListAdapter adapter = new CityListAdapter(this, cities);
        lvCities.setAdapter(adapter);
    }

    private void setEvents() {
        fabAddCity.setOnClickListener(new View.OnClickListener() {
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
        rlWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processExtendedWeather(v);
            }
        });
        tvExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processExtendedWeather(v);
            }
        });

        final Context context = this;
        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityListItem item = (CityListItem) parent.getItemAtPosition(position);
                String cityName = item.getCity();
                String country = item.getCountry();
                City city = new City(cityName, country);
                SharedPreferencesHelper.setChoosenCity(context, city);
                processNewCity(city);
            }
        });
        fabWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processExtendedWeather(v);
            }
        });
    }

    private void processNewCity(City city) {
        ApiWeather weather = getWeather(city);
        if (weather != null) {
            setWeather(weather);
        } else {
            //          show progress bar
            pvWeatherLoading.setVisibility(View.VISIBLE);
            tvWeatherLoading.setText(getString(R.string.textview_loading));
            rlWeatherView.setVisibility(View.GONE);
        }
    }

    private ApiWeather getWeather(City city) {
        ApiWeather weather = loadFromCache(city);
        if (weather != null) {
            return weather;
        } else {
            CityInt cityInt = dbHelper.getCityInt(city);
            String id = dbHelper.getCityId(cityInt);
            startPrimaryTask(city, cityInt, id);
            return null;
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

    private void processExtendedWeather(View v) {
        if (weatherShowState == WeatherShowState.Normal) {
            weatherShowState = WeatherShowState.ThreeDays;
            fillExtendedWeatherList();
            llExtendedWeather.setVisibility(View.VISIBLE);
            fabAddCity.setVisibility(View.GONE);
            tvExtend.setVisibility(View.GONE);
            rlCities.setVisibility(View.GONE);
        } else if (weatherShowState == WeatherShowState.ThreeDays) {
            if (v instanceof RelativeLayout) {
                weatherShowState = WeatherShowState.Normal;
                llExtendedWeather.setVisibility(View.GONE);
                fabAddCity.setVisibility(View.VISIBLE);
                tvExtend.setVisibility(View.VISIBLE);
                rlCities.setVisibility(View.VISIBLE);
            } else {
                lvExtendedWeather.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                fabWeek.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_up_white_48dp), false);
                weatherShowState = WeatherShowState.Week;
                fillExtendedWeatherList();
            }
        } else {
            lvExtendedWeather.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            fabWeek.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_numeric_7_box_outline_white_48dp), false);
            weatherShowState = WeatherShowState.Normal;
            llExtendedWeather.setVisibility(View.GONE);
            fabAddCity.setVisibility(View.VISIBLE);
            tvExtend.setVisibility(View.VISIBLE);
            rlCities.setVisibility(View.VISIBLE);
        }
    }

    private void fillExtendedWeatherList() {
        City choosenCity = SharedPreferencesHelper.getChoosenCity(this);
        int count = weatherShowState == WeatherShowState.Week ? 7 : 3;
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
                SharedPreferencesHelper.setChoosenCity(this, new City(cityName, country));
            } else {
                Toast.makeText(this, getString(R.string.txt_empty_place), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setWeather(ApiWeather weather) {
        try {
            City city = SharedPreferencesHelper.getChoosenCity(this);
            tvCity.setText(city.getName());
            ApiDay day = weather.getList().get(0);
            long dateTime = day.getDt() * 1000;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
            tvDate.setText(dateFormat.format(dateTime));
            String dayOfTheWeek = CommonMethods.getDayOfWeek(this, new Date(dateTime));
            tvDayOfWeek.setText(dayOfTheWeek);
            tvTemperature.setText(CommonMethods.getTemperatureString(day.getTemp().getTemperature()));
            tvTemperatureMin.setText(CommonMethods.getTemperatureString(day.getTemp().getTemperatureMin()));
            tvTemperatureMax.setText(CommonMethods.getTemperatureString(day.getTemp().getTemperatureMax()));
            ApiWeatherDescriptor descr = day.getWeather().get(0);
            tvWeather.setText(CommonMethods.getWeatherRu(this, descr.getWeather()));
            ivWeatherMain.setImageResource(CommonMethods.getBigWeatherImageId(this, descr.getWeather()));
            tvHumidity.setText(String.valueOf(day.getHumidity()));
            DecimalFormat df = new DecimalFormat("#");
            tvPressure.setText(df.format(day.getPressure()));
            df = new DecimalFormat("#.00");
            tvWind.setText(df.format(day.getSpeed()));
            tvClouds.setText(String.valueOf(day.getClouds()));
            fillCityListView();
            rlWeatherView.setVisibility(View.VISIBLE);
            startSecondaryTasks();
        } catch (NullPointerException e) {
            setNoData();
        }
    }

    public void setNoData() {
        pvWeatherLoading.setVisibility(View.GONE);
        tvWeatherLoading.setText(getString(R.string.textview_no_data));
        fillCityListView();
        startSecondaryTasks();
    }

    public void setProgressViewValue(int count) {
        if (count < Consts.RU_CITIES_COUNT) {
            float progress = (float) count / Consts.RU_CITIES_COUNT;
            pvIdsLoading.setProgress(progress);
        } else {
            rlIdsLoading.setVisibility(View.GONE);
        }
    }

    private void startPrimaryTask(City city, CityInt cityInt, String id) {
        cancelSecondaryTasks();
        getWeatherTask = new AsyncTaskAccumulator.GetWeatherTask(this, city, cityInt, id, Consts.DAYS);
        getWeatherTask.execute((Void) null);
    }

    public void startSecondaryTasks() {
        if (SharedPreferencesHelper.getIdLoadingState(this) == 1) {
            if (extractJsonTask == null && fillRuCitiesIdsTask == null) {
                if (Cache.JsonList.size() != Consts.RU_CITIES_COUNT) {
                    extractJsonTask = new AsyncTaskAccumulator.ExtractJsonTask(this);
                    extractJsonTask.execute((Void) null);
                }
                fillRuCitiesIdsTask = new AsyncTaskAccumulator.FillRuCitiesIdsTask(this);
                fillRuCitiesIdsTask.execute((Void) null);
            }
        }
    }

    private void cancelPrimaryTask() {
        if (getWeatherTask != null) {
            getWeatherTask.cancel(false);
            getWeatherTask = null;
        }
    }

    private void cancelSecondaryTasks() {
        if (extractJsonTask != null) {
            extractJsonTask.cancel(true);
            extractJsonTask = null;
        }
        if (fillRuCitiesIdsTask != null) {
            fillRuCitiesIdsTask.cancel(true);
            fillRuCitiesIdsTask = null;
        }
    }

}
