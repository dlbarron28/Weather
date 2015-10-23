package com.davidbarron.weather;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherDetail extends Fragment{
    private DetailsFragmentListener listener;
    private TextView city_name, sun_rise, sun_set, current_temp, temp_min, temp_max;
    private TextView humidity_value,  pressure_value, wind_name, clouds_name;
    private TextView weather_value, update;
    private ImageView current_weather_icon;
    private String packageName;
    private long rowID = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        setHasOptionsMenu(true);
        city_name = (TextView) view.findViewById(R.id.city_name);
        sun_rise = (TextView) view.findViewById(R.id.sun_rise);
        sun_set = (TextView) view.findViewById(R.id.sun_set);
        current_temp = (TextView) view.findViewById(R.id.current_temp);
        temp_min = (TextView) view.findViewById(R.id.temp_min);
        temp_max = (TextView) view.findViewById(R.id.temp_max);
        humidity_value = (TextView) view.findViewById(R.id.humidity_value);
        pressure_value = (TextView) view.findViewById(R.id.pressure_value);
        wind_name = (TextView) view.findViewById(R.id.wind_name);
        clouds_name = (TextView) view.findViewById(R.id.clouds_name);
        weather_value = (TextView) view.findViewById(R.id.weather_value);
        update = (TextView) view.findViewById(R.id.update);
        current_weather_icon = (ImageView) view.findViewById(R.id.current_weather_icon);

        Bundle arguments = getArguments();
        if (arguments != null)
            rowID = arguments.getLong(MainActivity.ROW_ID);
        Log.i(WeatherDetail.class.getName(), "Row ID: " + rowID);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_detail_menu, menu);
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_delete:
                deleteLocation();
                listener.onDeleteLocation();
                return true;
        }

        return super.onOptionsItemSelected(item); // call super's method
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        packageName = activity.getPackageName();
        listener = (DetailsFragmentListener) activity;
    }

    // remove DetailsFragmentListener when fragment detached
    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLocation();
    }

    public interface DetailsFragmentListener
    {
        // called when a contact is deleted
        public void onDeleteLocation();
    }
    private void loadLocation() {
        DatabaseDriver databaseDriver = new DatabaseDriver(getActivity());
        Cursor cursor = databaseDriver.getLocation(rowID);
        cursor.moveToFirst();
        String entry = cursor.getString(cursor.getColumnIndex("city")) + ", " + cursor.getString(cursor.getColumnIndex("state"));
        cursor.close();
        databaseDriver.close();
        new FetchWeatherTask().execute(entry);
    }
    private void deleteLocation() {
        DatabaseDriver databaseDriver = new DatabaseDriver(getActivity());
        databaseDriver.deleteLocation(rowID);
        databaseDriver.close();
    }

    private void setText(WeatherItem item) {
        city_name.setText(item.getCity());
        sun_rise.setText(item.getRise());
        sun_set.setText(item.getSet());
        current_temp.setText(item.getTemp_value());
        temp_min.setText(item.getTemp_min());
        temp_max.setText(item.getTemp_max());
        humidity_value.setText(item.getHumidity_value() + item.getHumidity_unit());
        pressure_value.setText(item.getPressure_value() + " " + item.getPressure_unit());
        wind_name.setText( item.getWind_value() + " " + item.getWind_name());
        clouds_name.setText(item.getClouds_name());
        weather_value.setText(item.getWeather_value());
        current_weather_icon.setImageResource(getResources().getIdentifier("weather_vclouds_" + item.getWeather_number(), "mipmap", packageName));
        update.setText(item.getUpdate());

    }
    public class FetchWeatherTask extends AsyncTask<String,Void,WeatherItem> {

        @Override
        protected WeatherItem doInBackground(String...params) {
            if(params!=null && params.length>0) {
                String loc = params[0];
                return new WeatherFetcher().fetchWeather(loc);
            }
            return null;
        }

        @Override
        protected void onPostExecute(WeatherItem item) {
            if(item != null) {
                setText(item);
            }
        }
    }
}
