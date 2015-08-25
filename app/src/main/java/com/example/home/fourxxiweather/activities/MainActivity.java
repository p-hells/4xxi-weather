package com.example.home.fourxxiweather.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.adapters.CityListAdapter;
import com.example.home.fourxxiweather.models.CityListItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //UI references
    ListView lvCities;
    TextView tvCity;
    TextView tvTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvCities = (ListView) findViewById(R.id.lvCities);
        tvCity = (TextView)findViewById(R.id.tvCity);
        tvTemperature = (TextView)findViewById(R.id.tvTemperature);
        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityListItem item = (CityListItem) parent.getItemAtPosition(position);
                processNewCity(item.getCity(), item.getTemperature());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<CityListItem> cities = new ArrayList<>();
        cities.add(new CityListItem(getString(R.string.city_Moscow),
                getString(R.string.country_Russia), "+ 20 C", 0));
        cities.add(new CityListItem(getString(R.string.city_St_Petersburg),
                getString(R.string.country_Russia), "+ 25 C", 0));
        CityListAdapter adapt = new CityListAdapter(this, cities);
        lvCities.setAdapter(adapt);
    }

    private void processNewCity(String city, String temperature){
        tvCity.setText(city);
        tvTemperature.setText(temperature);
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
