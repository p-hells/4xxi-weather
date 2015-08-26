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
import com.example.home.fourxxiweather.models.ExtendedWeatherItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExtendedWeatherListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater lInflater;
    ArrayList<ExtendedWeatherItem> objects;

    public ExtendedWeatherListAdapter(Context context, ArrayList<ExtendedWeatherItem> weather) {
        this.context = context;
        objects = weather;
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
        RelativeLayout layout = (RelativeLayout)convertView;
        if (layout == null) {
            layout = (RelativeLayout)lInflater.inflate(R.layout.extended_weather_item, parent, false);
        }
        ExtendedWeatherItem item = (ExtendedWeatherItem)getItem(position);

        String dateString = item.getDate();
        Date date;
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
        SimpleDateFormat dateFormatShort = new SimpleDateFormat("dd.MM");
        try {
            date = dateFormat.parse(dateString);
            dateString = dateFormatShort.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String dayOfWeek = item.getDayOfWeek();
        String temperature = item.getTemperature();

        TextView tvDate = (TextView) layout.findViewById(R.id.tvDateItem);
        TextView tvDayOfWeek = (TextView) layout.findViewById(R.id.tvDayOfWeekItem);
        TextView tvTemperature = (TextView) layout.findViewById(R.id.tvTempItem);
        ImageView ivWeather = (ImageView) layout.findViewById(R.id.ivExtendedWeatherPicItem);

        tvDate.setText(dateString);
        tvDayOfWeek.setText(dayOfWeek);
        tvTemperature.setText(temperature);
        ivWeather.setImageResource(item.getWeatherImageId());

        return layout;
    }
}