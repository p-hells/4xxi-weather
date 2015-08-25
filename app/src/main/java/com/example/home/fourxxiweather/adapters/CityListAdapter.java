package com.example.home.fourxxiweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.models.CityListItem;

import java.util.ArrayList;

public class CityListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater lInflater;
    ArrayList<CityListItem> objects;

    public CityListAdapter(Context context, ArrayList<CityListItem> cities) {
        this.context = context;
        objects = cities;
        lInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = (RelativeLayout) convertView;
        if (layout == null) {
            layout = (RelativeLayout) lInflater.inflate(R.layout.city_list_item, parent, false);
        }
        CityListItem item = (CityListItem) getItem(position);

        TextView tvCityItem = (TextView) layout.findViewById(R.id.tvCityItem);
        String city = item.getCity();
        tvCityItem.setText(city);

        TextView tvCountryItem = (TextView) layout.findViewById(R.id.tvCountryItem);
        String country = item.getCountry();
        tvCountryItem.setText(country);

        TextView tvTemp = (TextView) layout.findViewById(R.id.tvTemperatureItem);
        String temp = item.getTemperature();
        tvTemp.setText(temp);

        ImageView ivWeather = (ImageView) layout.findViewById(R.id.ivWeatherPicItem);
      //  ivWeather.setImageResource(item.getWeatherPicId());

        return layout;
    }
}