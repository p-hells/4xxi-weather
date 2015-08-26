package com.example.home.fourxxiweather.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.consts.Consts;
import com.example.home.fourxxiweather.models.City;
import com.example.home.fourxxiweather.models.CityInt;
import com.example.home.fourxxiweather.utils.AsyncTaskAccumulator;
import com.example.home.fourxxiweather.utils.DBHelper;
import com.example.home.fourxxiweather.utils.SharedPreferencesHelper;

public class SplashScreenActivity extends AppCompatActivity {

    DBHelper dbHelper;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        dbHelper = new DBHelper(this);
        check = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper.addRecordToCityTable(new City(getString(R.string.city_Moscow), getString(R.string.country_Russia)),
                new CityInt(getString(R.string.city_Moscow_int), getString(R.string.country_Russia_code)));
        dbHelper.addRecordToCityTable(new City(getString(R.string.city_St_Petersburg), getString(R.string.country_Russia)),
                new CityInt(getString(R.string.city_St_Petersburg_int), getString(R.string.country_Russia_code)));

        if (SharedPreferencesHelper.getChoosenCity(this).getName().equals("")) {
            check = true;
            loadData(new City(getString(R.string.city_Moscow),
                    getString(R.string.country_Russia)));
            loadData(new City(getString(R.string.city_St_Petersburg),
                    getString(R.string.country_Russia)));
            SharedPreferencesHelper.setChoosenCity(this, new City(getString(R.string.city_St_Petersburg),
                    getString(R.string.country_Russia)));
        } else {
            loadData(SharedPreferencesHelper.getChoosenCity(this));
        }
    }

    private void loadData(City city){
        CityInt cityInt = dbHelper.getCityInt(city);
        String id = dbHelper.getCityId(cityInt);
        AsyncTaskAccumulator.GetWeatherTask getWeatherTask = new AsyncTaskAccumulator.GetWeatherTask(this, city, cityInt, id, Consts.DAYS);
        getWeatherTask.execute((Void) null);

    }

    @Override
    public void onBackPressed() {
        //    super.onBackPressed();
    }

    public void processResult(){
        if (check) {
            check = false;
        } else {
            AsyncTaskAccumulator.EmptyTask emptyTask = new AsyncTaskAccumulator.EmptyTask(this);
            emptyTask.execute((Void)null);
        }
    }
}