package com.example.meteoapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CityAdapter extends ArrayAdapter<City> {
    private List<City> cities;

    public CityAdapter(Context context, List<City> cities) {
        super(context, 0, cities);
        this.cities = cities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        City city = cities.get(position);

        TextView cityName = convertView.findViewById(android.R.id.text1);
        TextView weatherInfo = convertView.findViewById(android.R.id.text2);

        cityName.setText(city.getName());
        weatherInfo.setText(city.getWeatherInfo());

        return convertView;
    }
}

